# ClockOverlay - A Simple Mobile App for Displaying Time Overlay During Presentations

## Overview

ClockOverlay is a mobile application designed to display a floating time overlay on the screen. This is particularly useful during presentations, allowing the presenter to keep track of time without switching away from their presentation content.
Thanks to my father for the idea, I made this app for him.

## Problem

During presentations, it can be challenging to keep track of time without disrupting the flow of the presentation. Traditional methods, such as checking a watch or a phone, can be distracting and unprofessional. There is a need for a non-intrusive way to monitor time while presenting.

## Solution

ClockOverlay solves this problem by providing a floating time overlay that remains on top of all other applications. This overlay can be moved and resized as needed, ensuring it does not obstruct important content on the screen. The application runs as a foreground service, ensuring it remains active and visible throughout the presentation.

## Features

- **Floating Time Overlay**: Displays the current time in a floating window.
- **Draggable and Resizable**: The overlay can be moved and resized to fit the presenter's needs.
- **Foreground Service**: Ensures the overlay remains active and visible.
- **Chronometer**: Includes a chronometer to track elapsed time.

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/MichealAPI/ClockOverlay.git
    ```
2. Open the project in your favorite IDE.
3. Build the project using Gradle.

## Usage

1. Grant the necessary permissions:
    - `SYSTEM_ALERT_WINDOW`
    - `FOREGROUND_SERVICE`
    - `FOREGROUND_SERVICE_SPECIAL_USE`

2. Start the `FloatingTimeService` from your main activity or any other component:
    ```kotlin
    val intent = Intent(this, FloatingTimeService::class.java)
    startService(intent)
    ```

3. Run through an emulator or physical device.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more information.
