# YouTube Comment Analyzer Android App

An Android application that analyzes YouTube video comments and identifies frequently asked questions by viewers.

## Features

- Accept YouTube video URLs
- Retrieve comments using YouTube Data API v3
- Identify questions from viewer comments
- Count and rank repeated questions
- Display top 50 most asked questions
- Performance optimized for fast analysis (3-6 seconds)

## Setup Instructions

### 1. Get YouTube Data API Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable YouTube Data API v3
4. Create credentials (API Key)
5. Copy the API key

### 2. Configure API Key

Open `YouTubeApiManager.kt` and replace the placeholder:

```kotlin
private val API_KEY = "YOUR_YOUTUBE_API_KEY_HERE" // Replace with your API key
```

### 3. Build and Run

1. Open the project in Android Studio
2. Sync Gradle dependencies
3. Run the app on an emulator or physical device

## App Components

### Main Activity
- Handles the main screen UI
- Accepts YouTube URL input
- Displays analysis results
- Manages background processing

### API Manager
- Communicates with YouTube Data API v3
- Extracts video ID from URLs
- Fetches video comments (limited to 300 for performance)

### Comment Parser
- Cleans and normalizes comment text
- Removes HTML tags and emojis
- Prepares data for analysis

### Question Detector
- Identifies comments containing questions
- Detects question words and patterns
- Filters out non-question content

### Question Counter
- Counts repeated questions
- Normalizes similar questions
- Ranks by frequency

## Performance Optimizations

- Limited to first 300 comments for faster processing
- Background threading for API calls
- Efficient counting algorithms
- Optimized text processing

## Usage

1. Enter a YouTube video URL in the input field
2. Click "Analyze Comments"
3. Wait for analysis to complete (3-6 seconds)
4. View the top 50 most asked questions with viewer counts

## Example Output

```
Top Viewer Questions

1. How to install Python? (42 viewers)
2. Why is my code not working? (30 viewers)
3. Can you make React tutorial? (18 viewers)
```

## Requirements

- Android 8.0+ (API level 28)
- Internet connection
- YouTube Data API key

## Future Improvements

- Detect viewer suggestions
- Sentiment analysis (positive/negative)
- Graphical analytics dashboard
- AI-based question grouping
- Export results to CSV/JSON

## Dependencies

- YouTube Data API v3
- Retrofit for HTTP requests
- Coroutines for background processing
- Jetpack Compose for UI
