# XTreeve
**XTreeve** is an advanced application designed for efficiently viewing and editing large XML files. It is built with performance in mind, enabling smooth navigation through complex XML structures, even with file sizes reaching several gigabytes. Whether you're working with massive datasets, configuration files, or detailed documents, XTreeve offers a responsive and intuitive interface for XML manipulation.

## Features

- **Efficient XML Viewing**: Seamlessly handle and view XML files of substantial size (1GB+).
- **Tree-based Navigation**: Explore XML content in a hierarchical tree structure, making it easy to understand complex relationships between elements.
- **Lazy Loading for Large XML**: Only load XML elements as they are needed, improving performance for large files.
- **Incremental Loading for Arrays**: Load large XML arrays in chunks to prevent overwhelming the UI.
- **Editing Support**: Modify individual XML nodes and attributes with ease. Edits are reflected in the tree view.
- **Search and Filtering**: Quickly locate specific elements or attributes within large files using the built-in search functionality.
- **Progressive Loading**: When switching between large files, memory is optimized by clearing previous content.
- **Lightweight Storage**: Uses an efficient in-memory database for intermediate storage of XML data.

## Technical Overview

XTreeve is built using JavaFX for a modern, responsive UI and integrates technologies like:

- **Jackson**: For XML parsing, enabling fast processing and manipulation of XML data.
- **TreeView with Lazy Loading**: The XML structure is represented as a tree view, with elements loaded incrementally to avoid performance bottlenecks on very large files.
  
## How to Use

### Viewing Large XML Files
- Open large XML files through the **File Open** dialog.
- Navigate the tree structure to view XML tags, attributes, and values.
- Expand nodes to lazily load their children.
- For arrays or lists of XML elements, load data incrementally by expanding specific sections.

### Editing XML Files
- Select any node from the tree view to see its details.
- Edit tag names, values, or attributes directly from the UI.
- Save your changes back to the XML file.

## Requirements

- **Java 21+**
- **Maven 3.x** (for building the project)
  
## How to Build

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/xtreeve.git
   ```
2. Navigate to the project directory:
   ```bash
   cd xtreeve
   ```
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn javafx:run
   ```

## Contributions
Contributions are welcome! Feel free to open issues for bugs, feature requests, or submit pull requests. Make sure to follow the contribution guidelines.
