#!/bin/bash

# Directory containing .class files
project_name="projects/banking_system"
input_directory="$project_name/target"

# Directory where JSON files will be saved
output_directory="$project_name/json"

# Create the output directory if it doesn't exist
mkdir -p "$output_directory"

# Process each .class file
find "$input_directory" -type f -name "*.class" | while read classfile; do
    # Extract the filename without extension
    filename=$(basename -- "$classfile")
    filename="${filename%.*}"

    # Define the output file path
    output_file="$output_directory/$filename.json"

    # Run Jvm2json on the class file and save the output
    # Specify the path to the classfile (-s) and to the output file (-t)
    jvm2json -s "$classfile" -t "$output_file"
done
    
# 'find "$input_directory" -type f -name "*.class" | while read classfile; do
#     filename=$(basename -- "$classfile")
#     filename="${filename%.*}"

#     output_file="$output_directory/$filename.json"

#     cat' "$output_file" | jq . > "$output_file"
# done