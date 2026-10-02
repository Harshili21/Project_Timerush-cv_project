#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Simple test script to verify FoundIt system components
"""

import os
import csv
from datetime import datetime

def test_csv_creation():
    """Test CSV file creation and writing"""
    csv_file = 'test_foundit_logs.csv'
    
    # Create CSV with headers
    with open(csv_file, 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['Timestamp', 'Object_Name', 'Confidence', 'Location_Tag'])
    
    # Add test data
    with open(csv_file, 'a', newline='') as file:
        writer = csv.writer(file)
        writer.writerow([
            datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
            'keys',
            '0.950',
            'Living Room'
        ])
    
    print(f"Test CSV created: {csv_file}")
    
    # Read and display
    with open(csv_file, 'r') as file:
        reader = csv.reader(file)
        for row in reader:
            print(row)
    
    # Clean up
    if os.path.exists(csv_file):
        os.remove(csv_file)
        print("Test file cleaned up")

def test_model_loading():
    """Test if the model file exists"""
    model_path = 'best.pt'
    if os.path.exists(model_path):
        print(f"Model file found: {model_path}")
        print(f"File size: {os.path.getsize(model_path)} bytes")
        return True
    else:
        print(f"Model file not found: {model_path}")
        return False

if __name__ == "__main__":
    print("Testing FoundIt components...")
    print("=" * 40)
    
    print("\n1. Testing CSV functionality:")
    test_csv_creation()
    
    print("\n2. Testing model file:")
    test_model_loading()
    
    print("\nTest completed!")
