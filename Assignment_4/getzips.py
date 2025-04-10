import os
import shutil

# Get the current directory
current_dir = os.getcwd()

# Create a temporary folder
temp_folder = os.path.join(current_dir, 'temp')
os.makedirs(temp_folder, exist_ok=True)

# Define the source folders
src_folder_main = os.path.join(current_dir, 'src', 'main', 'java')
src_folder_test = os.path.join(current_dir, 'src', 'test', 'java')

# Define the destination folders inside the temp folder (with the new names)
extract_src_folder = os.path.join(temp_folder, 'src')
extract_test_folder = os.path.join(temp_folder, 'test')

# Create the src and test folders inside the temp folder
for folder in [extract_src_folder, extract_test_folder]:
    os.makedirs(folder, exist_ok=True)

# Function to copy contents from source to destination
def copy_contents(src_folder, dest_folder):
    if os.path.exists(src_folder):
        for item in os.listdir(src_folder):
            src_item = os.path.join(src_folder, item)
            dest_item = os.path.join(dest_folder, item)
            
            if os.path.isdir(src_item):
                shutil.copytree(src_item, dest_item, dirs_exist_ok=True)
            else:
                shutil.copy2(src_item, dest_item)
        print(f"Contents of '{src_folder}' copied to '{dest_folder}'.")
    else:
        print(f"Source folder '{src_folder}' does not exist.")

# Copy the contents from src\main\java to src
copy_contents(src_folder_main, extract_src_folder)

# %%
# Copy the contents from src\test\java to test

copy_contents(src_folder_test, extract_test_folder)

# Zip the src and test folders inside the temp folder
shutil.make_archive(os.path.join(current_dir, 'Extracted_Folders'), 'zip', temp_folder)

# Clean up by removing the temporary folder after zipping
shutil.rmtree(temp_folder)

print("Both src and test folders have been zipped together into 'Extracted_Folders.zip' in the current directory.")
