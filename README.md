# Geometry Dash AI - Java Implementation

An advanced Java implementation of Geometry Dash, featuring a powerful physics engine, and an AI system using a genetic algorithm to procedurally generate unique and playable maps.

## ✨ Features
- **Extensive Game Engine** Precise physics and a component-based collision system (BoxBounds, TriangleBounds).
- **Various player states and mode** Cube, Ship, Ball, UFO, and Wave.
- **Dynamic portals that change gravity** Cube, Ship, Ball, UFO, and Wave.
- **Background parallax and sound systems for complete immersion..**
- **Artificial Intelligence and Level Generation:** Genetic Algorithm for evolutionary level creation. Hybrid Generation Model combining various techniques: Markov Chains for emulating sequences from existing levels.
- **Neural Network** LSTM for learning more complex and long-term dependencies in level structures.
- **Plugin system allows for easy addition of external generation models.**
- **Advanced Rating System (Fitness)** Multi-dimensional evaluation of level quality, including playability, replayability, variety, jump sequences, and difficulty progression.
- **Virtual Playtester (AIPlaytester)** Simulates gameplay to identify impossible sections and sudden difficulty spikes.
- **Graphical interface** Fully functional main menu and options screen. Ability to select the difficulty level of generated maps and your preferred AI model.

## 🎮 Controls
### Game Mode
- **Space** - Jump/Fly

## 🧬 Genetic algorithm
The game features an advanced level generation system based on a genetic algorithm that evaluates levels according to the following criteria:
- **Playability** - whether the level can be completed
- **Difficulty** - the right balance of obstacles
- **Diversity** - avoiding monotonous sections
- **Flow/Rhythm** - smoothness of gameplay

## 🚀 Starting
1. Clone the repository
2. Open the project in the IDE (IntelliJ IDEA/Eclipse)
3. Make sure the dependencies (including the deeplearning4j libraries) are configured correctly.
4. Run the `Main.java` class 
5. Select "Start Game" from the main menu
6. (Optional) Place your own generation models as .jar files in the plugins directory in the project's root folder to dynamically load them.

## 🛠️ Requirements
- Java 24+
- IDE with Gradle or Maven support (for dependency management)
- Graphic libraries (AWT/Swing),
- javax.sound.sampled (for sound) deeplearning4j (for the LSTM model)
