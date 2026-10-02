#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
FoundIt - Computer Vision Tool for Finding Misplaced Items

Phase 2: Memory (Data Logging)
Phase 4: Query System

This script provides real-time object detection with logging and search capabilities.
"""

import cv2
import pandas as pd
import csv
import os
from datetime import datetime, timedelta
from ultralytics import YOLO
import threading
import time
from collections import defaultdict

class FoundItSystem:
    def __init__(self, model_path='best.pt', csv_file='foundit_logs.csv', location_tag='Living Room'):
        """
        Initialize the FoundIt system
        
        Args:
            model_path: Path to the trained YOLOv8 model
            csv_file: Path to the CSV log file
            location_tag: Hardcoded location tag for logging
        """
        self.model = YOLO(model_path)
        self.csv_file = csv_file
        self.location_tag = location_tag
        self.last_seen = {}  # Track last seen times for each object
        self.cooldown_period = 5  # 5 seconds cooldown
        self.running = False
        
        # Initialize CSV file
        self._init_csv()
        
        # Target classes
        self.target_classes = ['remote', 'keys', 'glasses']
        
    def _init_csv(self):
        """Initialize the CSV file with headers if it doesn't exist"""
        if not os.path.exists(self.csv_file):
            with open(self.csv_file, 'w', newline='') as file:
                writer = csv.writer(file)
                writer.writerow(['Timestamp', 'Object_Name', 'Confidence', 'Location_Tag'])
            print(f"Created new log file: {self.csv_file}")
    
    def _log_detection(self, object_name, confidence):
        """
        Log detection to CSV file with cooldown logic
        
        Args:
            object_name: Name of detected object
            confidence: Confidence score (0-1)
        """
        current_time = datetime.now()
        
        # Check cooldown period
        if object_name in self.last_seen:
            time_diff = current_time - self.last_seen[object_name]
            if time_diff.total_seconds() < self.cooldown_period:
                return  # Skip logging due to cooldown
        
        # Log the detection
        with open(self.csv_file, 'a', newline='') as file:
            writer = csv.writer(file)
            writer.writerow([
                current_time.strftime('%Y-%m-%d %H:%M:%S'),
                object_name,
                f"{confidence:.3f}",
                self.location_tag
            ])
        
        self.last_seen[object_name] = current_time
        print(f"Logged: {object_name} (confidence: {confidence:.3f}) at {self.location_tag}")
    
    def _process_frame(self, frame):
        """
        Process a single frame for object detection
        
        Args:
            frame: OpenCV frame
            
        Returns:
            Processed frame with detections
        """
        results = self.model(frame, conf=0.25)
        
        for result in results:
            boxes = result.boxes
            names = result.names
            
            for box in boxes:
                # Get detection info
                cls_id = int(box.cls[0])
                confidence = float(box.conf[0])
                class_name = names[cls_id]
                
                # Check if it's one of our target classes
                if class_name in self.target_classes and confidence >= 0.25:
                    # Log the detection
                    self._log_detection(class_name, confidence)
                    
                    # Draw bounding box
                    x1, y1, x2, y2 = map(int, box.xyxy[0])
                    cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)
                    
                    # Add label
                    label = f"{class_name}: {confidence:.2f}"
                    cv2.putText(frame, label, (x1, y1-10), 
                               cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
        
        return frame
    
    def start_detection(self):
        """Start real-time detection with webcam"""
        print("Starting FoundIt detection system...")
        print("Press 'q' to quit detection")
        print("Press 's' to search for an item")
        
        cap = cv2.VideoCapture(0)
        
        if not cap.isOpened():
            print("Error: Could not open webcam")
            return
        
        self.running = True
        
        while self.running:
            ret, frame = cap.read()
            if not ret:
                print("Error: Could not read frame")
                break
            
            # Process frame for detection
            processed_frame = self._process_frame(frame)
            
            # Add status text
            cv2.putText(processed_frame, f"Location: {self.location_tag}", 
                       (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 255, 255), 2)
            cv2.putText(processed_frame, "Press 'q' to quit, 's' to search", 
                       (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)
            
            # Display frame
            cv2.imshow('FoundIt - Object Detection', processed_frame)
            
            # Handle key presses
            key = cv2.waitKey(1) & 0xFF
            if key == ord('q'):
                break
            elif key == ord('s'):
                self._search_interface()
        
        cap.release()
        cv2.destroyAllWindows()
        self.running = False
        print("Detection stopped")
    
    def _search_interface(self):
        """Simple search interface for querying logged data"""
        print("\n" + "="*50)
        print("FoundIt Search System")
        print("="*50)
        
        while True:
            query = input("Enter object name to search (or 'back' to return to detection): ").strip().lower()
            
            if query == 'back':
                break
            
            if not query:
                print("Please enter a valid object name")
                continue
            
            result = self.search_object(query)
            if result:
                print(f"\nLast known location for '{query}':")
                print(f"  Timestamp: {result['timestamp']}")
                print(f"  Location: {result['location']}")
                print(f"  Confidence: {result['confidence']}")
            else:
                print(f"No records found for '{query}'")
            
            print("-" * 30)
    
    def search_object(self, object_name):
        """
        Search for the last known location of an object
        
        Args:
            object_name: Name of the object to search for
            
        Returns:
            Dictionary with timestamp, location, and confidence, or None if not found
        """
        try:
            if not os.path.exists(self.csv_file):
                return None
            
            df = pd.read_csv(self.csv_file)
            
            # Filter by object name (case insensitive)
            object_df = df[df['Object_Name'].str.lower() == object_name.lower()]
            
            if object_df.empty:
                return None
            
            # Get the most recent entry
            latest_entry = object_df.iloc[-1]
            
            return {
                'timestamp': latest_entry['Timestamp'],
                'location': latest_entry['Location_Tag'],
                'confidence': latest_entry['Confidence']
            }
            
        except Exception as e:
            print(f"Error searching for object: {e}")
            return None
    
    def get_all_logs(self):
        """Get all logged data"""
        try:
            if not os.path.exists(self.csv_file):
                return None
            
            df = pd.read_csv(self.csv_file)
            return df
            
        except Exception as e:
            print(f"Error reading logs: {e}")
            return None

def main():
    """Main function to run the FoundIt system"""
    print("="*60)
    print("FoundIt - Computer Vision Tool for Finding Misplaced Items")
    print("="*60)
    
    # Initialize the system
    try:
        foundit = FoundItSystem(
            model_path='best.pt',
            csv_file='foundit_logs.csv',
            location_tag='Living Room'
        )
    except Exception as e:
        print(f"Error initializing system: {e}")
        return
    
    print("\nOptions:")
    print("1. Start real-time detection")
    print("2. Search for an item")
    print("3. View all logs")
    print("4. Exit")
    
    while True:
        choice = input("\nEnter your choice (1-4): ").strip()
        
        if choice == '1':
            foundit.start_detection()
        elif choice == '2':
            object_name = input("Enter object name to search: ").strip()
            result = foundit.search_object(object_name)
            if result:
                print(f"\nLast known location for '{object_name}':")
                print(f"  Timestamp: {result['timestamp']}")
                print(f"  Location: {result['location']}")
                print(f"  Confidence: {result['confidence']}")
            else:
                print(f"No records found for '{object_name}'")
        elif choice == '3':
            logs = foundit.get_all_logs()
            if logs is not None and not logs.empty:
                print("\nAll logged detections:")
                print(logs.to_string(index=False))
            else:
                print("No logs found")
        elif choice == '4':
            print("Goodbye!")
            break
        else:
            print("Invalid choice. Please enter 1-4")

if __name__ == "__main__":
    main()
