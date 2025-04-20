#!/bin/bash

if [ ! -f "docs/openapi.yaml" ]; then
    echo "Error: docs/openapi.yaml not found!"
    exit 1
fi

echo "Copying OpenAPI spec to nginx folder..."
cp -r docs docker/nginx/docs

echo "Documentation update complete!"
