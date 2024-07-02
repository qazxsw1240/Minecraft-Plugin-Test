#!/bin/sh


dir="$(pwd)"

echo "Now you work in the directory: $dir"

if [ "$1" = "" ]; then
  echo "You must set the destination directory"
  exit 1
fi

echo "Your jar destination is $1"

jar_file_dir="$(pwd)/build/libs"

echo "Find jar files on $jar_file_dir"

for f in "$jar_file_dir"/*
do
  case $f in
    *.jar)
      jar_file=$f
      break
      ;;
  esac
done

echo "Find jar file $jar_file"

mv "$jar_file" "$1"

echo "Successfully moved $jar_file to $1"

exit 0
