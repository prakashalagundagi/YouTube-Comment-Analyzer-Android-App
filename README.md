📱 YouTube Comment Analyzer (Android Application)
<p align="center"> <img src="https://github.com/user-attachments/assets/40ecfbdd-42f2-4487-ba5b-1eba9be4f5d9" width="600"/> </p>

The YouTube Comment Analyzer is an Android application that analyzes comments from YouTube videos and identifies the most frequently asked questions. It helps in understanding audience concerns, common doubts, and trending queries efficiently.

🎯 Objective

Extract comments from a given YouTube video

Identify question-based comments

Analyze and rank frequently asked questions

Provide quick insights into viewer interactions

❓ Why This Application Was Developed
<p align="center"> <img src="https://github.com/user-attachments/assets/321d31fd-e286-460d-ab9a-ef148ed668bf" width="600"/> </p>

With the rapid growth of online content, YouTube creators receive thousands of comments. Manually analyzing them is time-consuming.

This application helps to:

⏱️ Reduce analysis time

🧠 Understand viewer doubts quickly

📊 Provide structured insights

🎯 Improve content quality

💬 Enhance creator–audience interaction

✨ Key Features

🔗 Accepts YouTube video URLs

🌐 Uses YouTube Data API v3

❓ Detects questions from comments

🔁 Groups repeated questions

📊 Displays top 50 questions

⚡ Fast analysis (3–6 seconds)

⚙️ System Setup
1. API Configuration

Create project in Google Cloud Console

Enable YouTube Data API v3

Generate API Key

Add it to your project

2. Application Setup

Open in Android Studio

Sync Gradle

Run on emulator/device

🧩 Application Architecture
<p align="center"> <img src="https://github.com/user-attachments/assets/becbe6b6-5d02-42bf-9f26-83b521697d10" width="45%"/> <img src="https://github.com/user-attachments/assets/3459fce1-886b-45bc-ac27-16e273a61a12" width="45%"/> </p>
🔹 Main Activity

Handles UI

Accepts input

Displays results

🔹 API Manager

Fetches comments

Extracts video ID

🔹 Comment Parser

Cleans text

🔹 Question Detector

Detects question patterns

🔹 Question Counter

Counts & ranks questions

⚡ Performance Optimization

🚀 Limited to 300 comments

🔄 Uses Kotlin Coroutines

🧠 Efficient text processing

⏱️ Fast response

📊 How to Use
<p align="center"> <img src="https://github.com/user-attachments/assets/7535b3a7-a2e3-48d1-b5b1-67c61b327ed7" width="45%"/> <img src="https://github.com/user-attachments/assets/7535b3a7-a2e3-48d1-b5b1-67c61b327ed7" width="45%"/> </p>

📥 Enter YouTube URL

▶️ Click Analyze Comments

⏳ Wait 3–6 seconds

📈 View top questions

🔍 Analyze insights

🎥 Demo Video
<p align="center"> <a href="https://github.com/user-attachments/assets/7535b3a7-a2e3-48d1-b5b1-67c61b327ed7"> ▶️ Click to Watch Demo </a> </p>
📌 Sample Output
<p align="center"> <img src="https://github.com/user-attachments/assets/7de2d47b-6ebf-4add-b326-0cfcca0be07c" width="45%"/> <img src="https://github.com/user-attachments/assets/b2707d9e-03b4-4d4e-9934-f199091dfa98" width="45%"/> </p>

==============================
📌 Sample Output
==============================

Top Viewer Questions:

- How to install Python? (42 viewers)
- Why is my code not working? (30 viewers)
- Can you provide a React tutorial? (18 viewers)


==============================
📋 Requirements
==============================

- Android 8.0+
- Internet connection
- YouTube API Key


==============================
🔮 Future Enhancements
==============================

- Sentiment analysis
- Suggestion detection
- Analytics dashboard
- AI-based grouping
- Export (CSV / JSON)


==============================
🛠️ Technologies Used
==============================

- YouTube Data API v3
- Retrofit
- Kotlin Coroutines
- Jetpack Compose
