import bpy
import sys

#get the arguments passed from command line after '--' 
#these will be the file import and export paths
argv = sys.argv
argv = argv[argv.index("--") + 1:]

#import the mesh
bpy.ops.import_mesh.stl(filepath=argv[0])

#bring mesh to view (if have blender open)
#bpy.ops.object.origin_set(type='GEOMETRY_ORIGIN')

ob = bpy.context.object
me = ob.data
op = bpy.ops.object

#scaling models
bpy.context.object.scale[0] = 0.1
bpy.context.object.scale[1] = 0.1
bpy.context.object.scale[2] = 0.1

#get polygon count and calculate decimation ratio based on target count
faces = len(me.polygons)
print(faces)

dec_ratio = 1/(faces/200000)
print(dec_ratio)


#apply decimate modifier with ratio calculated
op.modifier_add(type='DECIMATE')
ob.modifiers["Decimate"].use_collapse_triangulate = True
ob.modifiers["Decimate"].ratio = dec_ratio


#apply smooth shading 
op.shade_smooth()
'''
#toggle into edit mode and apply smoothing to faces/vertices 
op.editmode_toggle()
bpy.ops.mesh.faces_shade_smooth()
#bpy.ops.mesh.mark_sharp(clear=True, use_verts=True)
op.editmode_toggle() 
'''

print('starting material bit')

ob1 = bpy.context.active_object

mat = bpy.data.materials.get("Material")

if mat is None:
    # create material
    mat = bpy.data.materials.new(name="Material")

if ob1.data.materials:
    # assign to 1st material slot
    ob.data.materials[0] = mat
else:
    # no slots
    ob1.data.materials.append(mat)
    
#bpy.ops.material.new()

if (argv[2] == "brain"):
    ob.active_material.diffuse_color = (0.8, 0.478447, 0.262488)
elif (argv[2] == "lung"):
   ob.active_material.diffuse_color = (0.8, 0.3306, 0.125706)
elif (argv[2] == "heart"):
    ob.active_material.diffuse_color = (0.8, 0.364718, 0.186337)
elif (argv[2] == "skin"):
    ob.active_material.diffuse_color = (0.8, 0.478447, 0.262488)
elif (argv[2] == "bone"):
    ob.active_material.diffuse_color = (0.8, 0.654527, 0.71174)


ob.active_material.use_transparency = True
mat.alpha = 0.9
#ob1.show_transparent = True


#export the fbx - using only selected mesh
#use path_mode to include the applied material 

if (argv[3] == "fbx"):
    bpy.ops.export_scene.fbx(filepath=argv[1], check_existing=True, use_selection=True, global_scale=0.2, path_mode='COPY', embed_textures=True, object_types={'MESH'})
else:
    bpy.ops.export_scene.glb(filepath=argv[1], export_selected=True, export_apply=True, export_materials=True)

'''
def import_material(matname):
    from os.path import join

    path_to_scripts = bpy.utils.script_paths()[0]
    path_to_script = join(path_to_scripts, 'pbr_node')

    material_locator = "\\NodeTree\\"
    file_name = "glTF2.blend"    

    opath = "//" + file_name + material_locator + matname
    dpath = join(path_to_script, file_name) + material_locator

    bpy.ops.wm.link(
            filepath=opath,     # "//filename.blend\\Folder\\"
            filename=matname,   # "material_name
            directory=dpath,    # "fullpath + \\Folder\\
            filemode=1,
            link=False,
            autoselect=False,
            active_layer=True,
            instance_groups=False,
            relative_path=True)


import_material("glTF Specular Glossiness")

bpy.context.scene.render.engine = 'CYCLES'


bpy.ops.material.new()
bpy.ops.cycles.use_shading_nodes()

bpy.data.node_groups["Shader Nodetree"].nodes["Group"].inputs[1].default_value = (1, 0.138177, 0.0696344, 1)
bpy.data.node_groups["Shader Nodetree"].nodes["Group"].inputs[1].default_value = (1, 0.138177, 0.0696344, 1)
bpy.data.node_groups["Shader Nodetree"].nodes["Group"].inputs[3].default_value = 0.238095
bpy.data.node_groups["Shader Nodetree"].nodes["Group"].inputs[5].default_value = 0.257143
'''
