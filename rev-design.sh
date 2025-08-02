#!/bin/bash

# Rev-Design Command Line Interface
# Usage: ./rev-design.sh [options] [project-path]

set -e

PROJECT_PATH="."
TARGET=""
FORMAT="markdown"
HELP=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --target)
      TARGET="$2"
      shift 2
      ;;
    --format)
      FORMAT="$2"
      shift 2
      ;;
    --help)
      HELP=true
      shift
      ;;
    -*)
      echo "Unknown option $1"
      exit 1
      ;;
    *)
      PROJECT_PATH="$1"
      shift
      ;;
  esac
done

if [[ "$HELP" == true ]]; then
  cat << EOF
Rev-Design - Reverse Engineering Design Documentation Tool

USAGE:
    ./rev-design.sh [OPTIONS] [PROJECT_PATH]

OPTIONS:
    --target <type>     Generate specific document type only
                       Values: architecture, api, database, dataflow, interfaces
    --format <format>   Output format (default: markdown)
                       Values: markdown, json
    --help             Show this help message

EXAMPLES:
    # Analyze current directory
    ./rev-design.sh .
    
    # Analyze specific project
    ./rev-design.sh /path/to/project
    
    # Generate only architecture document
    ./rev-design.sh --target architecture .
    
    # Generate all documents for Android project
    ./rev-design.sh ./my-android-app

GENERATED FILES:
    docs/reverse/\${PROJECT_NAME}-architecture.md    - Architecture overview
    docs/reverse/\${PROJECT_NAME}-dataflow.md        - Data flow diagrams
    docs/reverse/\${PROJECT_NAME}-api-specs.md       - API specifications
    docs/reverse/\${PROJECT_NAME}-database.md        - Database design
    docs/reverse/\${PROJECT_NAME}-interfaces.kt      - Type definitions

SUPPORTED PROJECTS:
    - Android (Kotlin/Java)
    - Spring Boot
    - React/Vue/Angular
    - Node.js/Express
    - Python (FastAPI/Django)

For more information, see the documentation in docs/reverse/
EOF
  exit 0
fi

echo "🔍 Rev-Design Analysis Tool"
echo "=========================="
echo "Project Path: $PROJECT_PATH"
echo "Target: ${TARGET:-all}"
echo "Format: $FORMAT"
echo ""

# Check if project path exists
if [[ ! -d "$PROJECT_PATH" ]]; then
  echo "❌ Error: Project path '$PROJECT_PATH' does not exist"
  exit 1
fi

# Check if Python is available
if ! command -v python3 &> /dev/null; then
  echo "❌ Error: Python 3 is required but not installed"
  exit 1
fi

# Get the directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REV_DESIGN_SCRIPT="$SCRIPT_DIR/rev-design.py"

# Check if the Python script exists
if [[ ! -f "$REV_DESIGN_SCRIPT" ]]; then
  echo "❌ Error: rev-design.py not found at $REV_DESIGN_SCRIPT"
  exit 1
fi

# Run the analysis
echo "🚀 Starting reverse engineering analysis..."
python3 "$REV_DESIGN_SCRIPT" "$PROJECT_PATH"

# Show results
DOCS_DIR="$PROJECT_PATH/docs/reverse"
if [[ -d "$DOCS_DIR" ]]; then
  echo ""
  echo "✅ Analysis complete! Generated documents:"
  ls -la "$DOCS_DIR"/*.md "$DOCS_DIR"/*.kt 2>/dev/null | while read -r line; do
    echo "   📄 $line"
  done
  
  echo ""
  echo "💡 Next steps:"
  echo "   - Review the generated documentation"
  echo "   - Customize the analysis for your specific needs"
  echo "   - Use the documents for project onboarding"
  echo "   - Update architecture decisions based on findings"
else
  echo "❌ Error: No documents were generated"
  exit 1
fi