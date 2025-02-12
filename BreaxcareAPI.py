from flask import Flask, request, jsonify
import cv2
import numpy as np
import matplotlib.pyplot as plt
from tensorflow.keras.preprocessing import image
from io import BytesIO
from PIL import Image

app = Flask(__name__)

@app.route('/')
def home():
    return "Welcome to the Image Processing API! Use the /process-image endpoint to process images."

@app.route('/process-image', methods=['POST'])
def process_image():
    try:
        # Check if an image file is included in the request
        if 'image' not in request.files:
            return jsonify({'error': 'No image uploaded'}), 400

        # Read the uploaded image
        image_file = request.files['image']
        img = Image.open(image_file).convert('RGB')
        img = np.array(img)

        # Convert image to grayscale
        img_gray = cv2.cvtColor(img, cv2.COLOR_RGB2GRAY)

        # Apply edge detection
        edges = cv2.Canny(img_gray, threshold1=350, threshold2=400)

        # Find only the outer contour
        contours, _ = cv2.findContours(edges, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

        # Analyze the outer contour
        get_colors = None
        for contour in contours:
            area = cv2.contourArea(contour)
            perimeter = cv2.arcLength(contour, True)
            if perimeter == 0:  # Avoid division by zero
                continue


            circularity = (4 * np.pi * area) / (perimeter * perimeter)
            circularity_micro = circularity * 1_000_000 

            # Determine color based on circularity
            if 10_000 < circularity_micro < 40_000:
                get_colors = 'green'
            elif circularity < 10_000 :
                get_colors = 'blue'
            else:
                get_colors = 'red'

        # Additional analysis for white dots (calcium deposits)
        _, threshold_img = cv2.threshold(img_gray, 200, 255, cv2.THRESH_BINARY)
        white_dots_count = cv2.countNonZero(threshold_img)
        total_pixels = img_gray.shape[0] * img_gray.shape[1]
        white_dots_percentage = (white_dots_count / total_pixels) * 100

        # Determine abnormality
        result = 'Abnormal' if (get_colors == 'red' or white_dots_percentage > 1.5) else 'Normal'

        return jsonify({
            'roundness_color': get_colors,
            'white_dots_percentage': white_dots_percentage,
            'white_dots_count': white_dots_count,
            'result': result,
            'circularity': circularity_micro 
        })

    except Exception as e:
        return jsonify({'error': str(e)}), 500


if __name__ == '__main__':
    app.run(host='172.20.10.2', port=5000, debug=True)
