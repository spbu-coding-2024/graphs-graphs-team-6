# Graphs-Graphs 
A desktop app to analyse graphs.

## Features
### Main Algorithms
- Louvain community detection (used [Jetbrains Louvain algorithm](https://github.com/JetBrains-Research/louvain));
- Key vertices search (based on [harmonic centrality](https://infoscience.epfl.ch/entities/publication/7864800f-6d09-4bb4-bb09-f12a335fca92));
- Kamada Kawai graph layout.

### Classic Algorithms
- Tarjan strongly connected components;
- Kruscal minimal spanning tree (forest);
- Cycle detection;
- Dijkstra shortest path;
- Bridge search;
- Ford-Bellman shortest path.

### Actions
- Load & Save graph from/to Neo4j;
- Load & Save graph as JSON;
- Load & Save graph from/to SQLite `WIP`.

## Get Started

```declarative
./gradlew run
```