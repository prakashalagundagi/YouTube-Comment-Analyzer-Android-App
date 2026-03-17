📱 YouTube Comment Analyzer (Android Application)
<img width="800" height="600" alt="image" src="https://github.com/user-attachments/assets/40ecfbdd-42f2-4487-ba5b-1eba9be4f5d9" />


The YouTube Comment Analyzer is an Android-based application designed to analyze user comments from YouTube videos and identify the most frequently asked questions. The application helps in understanding audience concerns, common doubts, and trending queries in a structured and efficient manner.

🎯 Objective

The primary objective of this application is to:

Extract comments from a given YouTube video

Identify question-based comments

Analyze and rank frequently asked questions

Provide quick insights into viewer interactions

❓ Why This Application Was Developed
<img width="1024" height="576" alt="image" src="https://github.com/user-attachments/assets/321d31fd-e286-460d-ab9a-ef148ed668bf" />


With the rapid growth of online content, YouTube creators receive thousands of comments on their videos. Manually reading and analyzing these comments is time-consuming and inefficient.

This application was developed to:

⏱️ Reduce the time required to analyze large volumes of comments

🧠 Help creators understand common viewer doubts quickly

📊 Provide structured insights instead of raw comment data

🎯 Improve content quality by focusing on frequently asked questions

💬 Enhance interaction between creators and their audience

✨ Key Features

🔗 Accepts YouTube video URLs

🌐 Retrieves comments using YouTube Data API v3

❓ Detects questions from user comments

🔁 Groups and counts repeated questions

📊 Displays top 50 most frequently asked questions

⚡ Optimized for fast processing (3–6 seconds)

⚙️ System Setup
1. API Configuration

Create a project in Google Cloud Console

Enable YouTube Data API v3

Generate an API Key

Integrate the key into the application

2. Application Setup

Open the project in Android Studio

Sync Gradle dependencies

Run on emulator or physical device

🧩 Application Architecture
<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/becbe6b6-5d02-42bf-9f26-83b521697d10" /><img width="1024" height="937" alt="image" src="https://github.com/user-attachments/assets/3459fce1-886b-45bc-ac27-16e273a61a12" />


🔹 Main Activity

Manages user interface

Accepts video URL input

Displays analysis results

Handles background execution

🔹 API Manager

Communicates with YouTube Data API

Extracts video ID from URL

Fetches up to 300 comments for efficiency

🔹 Comment Parser

Cleans and normalizes comment text

🔹 Question Detector

Identifies question patterns

🔹 Question Counter

Counts and ranks questions

⚡ Performance Optimization

🚀 Limits processing to first 300 comments

🔄 Uses Kotlin Coroutines for background tasks

🧠 Efficient text processing algorithms

⏱️ Ensures quick response time

📊 How to Use


https://github.com/user-attachments/assets/7535b3a7-a2e3-48d1-b5b1-67c61b327ed7



📥 Enter Video URL
https://github.com/user-attachments/assets/7535b3a7-a2e3-48d1-b5b1-67c61b327ed7
▶️ Start Analysis
Tap Analyze Comments

⏳ Wait
Processing takes 3–6 seconds

📈 View Results
Top 50 questions will be shown

🔍 Analyze Insights
Understand user queries easily

🎥 Demo Video
https://github.com/user-attachments/assets/7535b3a7-a2e3-48d1-b5b1-67c61b327ed7
📌 Sample Output
![WhatsApp Image 2026-03-17 at 10 00 06 PM](https://github.com/user-attachments/assets/7de2d47b-6ebf-4add-b326-0cfcca0be07c)
![WhatsApp Image 2026-03-17 at 10 00 06 PM (1)](https://github.com/user-attachments/assets/b2707d9e-03b4-4d4e-9934-f199091dfa98)

Top Viewer Questions:

How to install Python? (42 viewers)

Why is my code not working? (30 viewers)

Can you provide a React tutorial? (18 viewers)

📋 System Requirements

📱 Android 8.0+ (API Level 28 or higher)

🌐 Internet connection

🔑 YouTube API Key

🔮 Future Enhancements

📈 Sentiment analysis

💡 Suggestion detection

📊 Graph dashboard

🤖 AI grouping

📤 Export data

🛠️ Technologies Used

YouTube Data API v3

Retrofit

Kotlin Coroutines

Jetpack Compose
