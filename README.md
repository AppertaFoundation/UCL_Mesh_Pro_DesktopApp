# Mesh_Pro_DesktopApp

Description: Multi-threaded Java/JavaFX application that runs Blender 3D modelling software in the background and processes 3D meshes ready for VR applicaitons (decimates surface, adds material, converts file format etc). This app was made to process medical CT segmentations (from .stl format to gtlf format) to load into Microsoft's HoloLens device using <a href=https://github.com/CaroloS/HoloCT >HoloCT </a> app. <br> <br>
Made in collaboration with Great Ormond Street Hospital, NHS ‘Digital Research, Informatics and Virtual Environments’ unit (DRIVE): https://www.gosh.nhs.uk/news/great-ormond-street-hospital-partner-microsoft-transform-healthcare-using-artificial-intelligence and UCL industry exchange (IXN) network: http://ixn.org.uk/. 

Video Demonstration here: https://youtu.be/1UDwQpKwpLY

## Authors 
This project was developed by UCL Computer Science students as part of the UCL Industry Exchange Network (http://ixn.org.uk) which pairs university students with industry as part of their curriculum.
Inventor and project lead - Dr Dean Mohamedally, d.mohamedally@ucl.ac.uk
MSc Student - Dr Caroline Smith

## Pre-requisites & Development tool
•	Java SE Development Kit:  1.8.0_171 <br>
•	Blender v2.79b

## Note about dependencies
Blender needs to be downloaded and placed in the same directory as the executable jar file for the app to function. Blender can be downloaded here: https://www.blender.org/download/<br><

And for glTF export support, the glTF exporter from the Khronos group needs to be installed to Blender as an add-on as here:
https://github.com/KhronosGroup/glTF-Blender-Exporter <br>

‘decimate.py’ script needs to be in the same directory as the executable jar file. This script can also be run directly in Blender. <br>

Blender scripting tutorial here: https://www.lynda.com/Blender-tutorials/Python-Scripting-Blender/486043-2.html

* Mesh_Pro_DesktopApp is provided under a GNU AFFERO GENERAL PUBLIC LICENSE and all terms of that licence apply (see LICENSE.txt). Use of the Mesh_Pro_DesktopApp or code is entirely at your own risk. Neither the Carolos nor DRIVE accept any responsibility for loss or damage to any person, property or reputation as a result of using the software or code. No warranty is provided by any party, implied or otherwise. This software and code is not guaranteed safe to use in a clinical or other environment and you should make your own assessment on the suitability for such use. Installation of any Mesh_Pro_DesktopApp software, indicates acceptance of this disclaimer. A supported and maintained version of Mesh_Pro_DesktopApp is available via Carolos partner DRIVE.
