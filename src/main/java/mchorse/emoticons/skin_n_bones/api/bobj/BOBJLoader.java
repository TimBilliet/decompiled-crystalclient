package mchorse.emoticons.skin_n_bones.api.bobj;

import co.crystaldev.client.util.javax.Matrix4f;
import co.crystaldev.client.util.javax.Vector2f;
import co.crystaldev.client.util.javax.Vector3f;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class BOBJLoader {
    public static void merge(BOBJData to, BOBJData from) {
        int vertSize = to.vertices.size();
        int normSize = to.normals.size();
        int textSize = to.textures.size();
        to.vertices.addAll(from.vertices);
        to.normals.addAll(from.normals);
        to.textures.addAll(from.textures);
        to.armatures.putAll(from.armatures);
        for (BOBJMesh mesh : from.meshes) {
            BOBJMesh newMesh = mesh.add(vertSize, normSize, textSize);
            newMesh.armature = to.armatures.get(newMesh.armatureName);
            to.meshes.add(newMesh);
        }
    }

    public static List<String> readAllLines(InputStream stream) throws Exception {
        List<String> list = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = br.readLine()) != null)
                list.add(line);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static BOBJData readData(InputStream stream) throws Exception {
        List<String> lines = readAllLines(stream);
        List<Vertex> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<BOBJMesh> objects = new ArrayList<>();
        Map<String, BOBJAction> actions = new HashMap<>();
        Map<String, BOBJArmature> armatures = new HashMap<>();
        BOBJMesh mesh = null;
        BOBJAction action = null;
        BOBJGroup group = null;
        BOBJChannel channel = null;
        BOBJArmature armature = null;
        BOBJBone bone = null;
        Vertex vertex = null;
        int i = 0;
        for (String line : lines) {
            String[] tokens = line.split("\\s");
            String first = tokens[0];
            if (first.equals("o")) {
                objects.add(mesh = new BOBJMesh(tokens[1]));
                armature = null;
                vertex = null;
                continue;
            }
            if (first.equals("o_arm")) {
                mesh.armatureName = tokens[1];
                continue;
            }
            if (first.equals("v")) {
                if (vertex != null)
                    vertex.eliminateTinyWeights();
                vertices.add(vertex = new Vertex(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                continue;
            }
            if (first.equals("vw")) {
                float weight = Float.parseFloat(tokens[2]);
                if (weight != 0.0F)
                    vertex.weights.add(new Weight(tokens[1], weight));
                continue;
            }
            if (first.equals("vt")) {
                textures.add(new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
                continue;
            }
            if (first.equals("vn")) {
                normals.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                continue;
            }
            if (first.equals("f")) {
                mesh.faces.add(new Face(tokens[1], tokens[2], tokens[3]));
                continue;
            }
            if (first.equals("arm_name")) {
                i = 0;
                bone = null;
                armature = new BOBJArmature(tokens[1]);
                armatures.put(armature.name, armature);
                continue;
            }
            if (first.equals("arm_action")) {
                armature.action = tokens[1];
                continue;
            }
            if (first.equals("arm_bone")) {
                Vector3f tail = new Vector3f(Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4]), Float.parseFloat(tokens[5]));
                Matrix4f boneMat = new Matrix4f();
                float[] mat = new float[16];
                for (int j = 6; j < 22; j++)
                    mat[j - 6] = Float.parseFloat(tokens[j]);
                boneMat.set(mat);
                bone = new BOBJBone(i++, tokens[1], tokens[2], tail, boneMat);
                armature.addBone(bone);
                continue;
            }
            if (first.equals("arm_ik") && tokens.length >= 2) {
                BOBJBone target = armature.bones.get(tokens[1]);
                if (bone == null) {
                    System.out.println("Found IK modifier in BOBJ, but bone " + tokens[1] + " doesn't exist...");
                    continue;
                }
                int chain = (tokens.length >= 3) ? Integer.parseInt(tokens[2]) : 1;
                boolean stick = (tokens.length >= 4 && tokens[3].equals("true"));
                bone.addModifier(new BOBJBoneModifier(target, chain, stick));
                continue;
            }
            if (first.equals("an")) {
                actions.put(tokens[1], action = new BOBJAction(tokens[1]));
                continue;
            }
            if (first.equals("ao")) {
                action.groups.put(tokens[1], group = new BOBJGroup(tokens[1]));
                continue;
            }
            if (first.equals("ag")) {
                group.channels.add(channel = new BOBJChannel(tokens[1], Integer.parseInt(tokens[2])));
                continue;
            }
            if (first.equals("kf"))
                channel.keyframes.add(BOBJKeyframe.parse(tokens));
        }
        if (vertex != null)
            vertex.eliminateTinyWeights();
        return new BOBJData(vertices, textures, normals, objects, actions, armatures);
    }

    public static Map<String, CompiledData> loadMeshes(BOBJData data) {
        Map<String, CompiledData> meshes = new HashMap<>();
        for (BOBJMesh mesh : data.meshes) {
            List<Integer> indices = new ArrayList<>();
            List<Face> facesList = mesh.faces;
            int[] boneIndicesArr = new int[facesList.size() * 3 * 4];
            float[] weightsArr = new float[facesList.size() * 3 * 4];
            float[] posArr = new float[facesList.size() * 3 * 4];
            float[] textCoordArr = new float[facesList.size() * 3 * 2];
            float[] normArr = new float[facesList.size() * 3 * 3];
            Arrays.fill(boneIndicesArr, -1);
            Arrays.fill(weightsArr, -1.0F);
            int i = 0;
            for (Face face : facesList) {
                for (IndexGroup indValue : face.idxGroups) {
                    processFaceVertex(i, indValue, mesh, data, indices, posArr, textCoordArr, normArr, weightsArr, boneIndicesArr);
                    i++;
                }
            }
            Integer[] integerArray = indices.<Integer>toArray(new Integer[0]);
            int[] indicesArr = ArrayUtils.toPrimitive(integerArray);
            meshes.put(mesh.name, new CompiledData(posArr, textCoordArr, normArr, weightsArr, boneIndicesArr, indicesArr, mesh));
        }
        return meshes;
    }

    public static CompiledData loadMesh(BOBJData data) {
        List<Integer> indices = new ArrayList<>();
        List<Face> facesList = new ArrayList<>();
        for (BOBJMesh mesh : data.meshes)
            facesList.addAll(mesh.faces);
        float[] posArr = new float[facesList.size() * 3 * 4];
        float[] textCoordArr = new float[facesList.size() * 3 * 2];
        float[] normArr = new float[facesList.size() * 3 * 3];
        int i = 0;
        for (Face face : facesList) {
            for (IndexGroup indValue : face.idxGroups) {
                processFaceVertex(i, indValue, null, data, indices, posArr, textCoordArr, normArr, null, null);
                i++;
            }
        }
        Integer[] integerArray = indices.<Integer>toArray(new Integer[0]);
        int[] indicesArr = ArrayUtils.toPrimitive(integerArray);
        return new CompiledData(posArr, textCoordArr, normArr, null, null, indicesArr, null);
    }

    private static void processFaceVertex(int index, IndexGroup indices, BOBJMesh mesh, BOBJData data, List<Integer> indicesList, float[] posArr, float[] texCoordArr, float[] normArr, float[] weightsArr, int[] boneIndicesArr) {
        indicesList.add(Integer.valueOf(index));
        if (indices.idxPos >= 0) {
            Vertex vec = data.vertices.get(indices.idxPos);
            posArr[index * 4] = vec.x;
            posArr[index * 4 + 1] = vec.y;
            posArr[index * 4 + 2] = vec.z;
            posArr[index * 4 + 3] = 1.0F;
            if (mesh != null)
                for (int i = 0, c = Math.min(vec.weights.size(), 4); i < c; i++) {
                    Weight weight = vec.weights.get(i);
                    BOBJBone bone = mesh.armature.bones.get(weight.name);
                    weightsArr[index * 4 + i] = (bone == null) ? 0.0F : weight.factor;
                    boneIndicesArr[index * 4 + i] = (bone == null) ? -1 : bone.index;
                }
        }
        if (indices.idxTextCoord >= 0) {
            Vector2f textCoord = data.textures.get(indices.idxTextCoord);
            texCoordArr[index * 2] = textCoord.x;
            texCoordArr[index * 2 + 1] = 1.0F - textCoord.y;
        }
        if (indices.idxVecNormal >= 0) {
            Vector3f vecNorm = data.normals.get(indices.idxVecNormal);
            normArr[index * 3] = vecNorm.x;
            normArr[index * 3 + 1] = vecNorm.y;
            normArr[index * 3 + 2] = vecNorm.z;
        }
    }

    public static class BOBJMesh {
        public String name;

        public List<Face> faces = new ArrayList<>();

        public String armatureName;

        public BOBJArmature armature;

        public BOBJMesh(String name) {
            this.name = name;
        }

        public BOBJMesh add(int vertices, int normals, int textures) {
            BOBJMesh mesh = new BOBJMesh(this.name);
            mesh.armatureName = this.armatureName;
            mesh.armature = this.armature;
            for (Face face : this.faces)
                mesh.faces.add(face.add(vertices, normals, textures));
            return mesh;
        }
    }

    public static class Face {
        public IndexGroup[] idxGroups = new IndexGroup[3];

        public Face(String v1, String v2, String v3) {
            this.idxGroups[0] = parseLine(v1);
            this.idxGroups[1] = parseLine(v2);
            this.idxGroups[2] = parseLine(v3);
        }

        public Face() {
        }

        private IndexGroup parseLine(String line) {
            IndexGroup idxGroup = new IndexGroup();
            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
            if (length > 1) {
                String textCoord = lineTokens[1];
                if (!textCoord.isEmpty())
                    idxGroup.idxTextCoord = Integer.parseInt(textCoord) - 1;
                if (length > 2)
                    idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
            }
            return idxGroup;
        }

        public Face add(int v, int n, int t) {
            Face face = new Face();
            for (int i = 0; i < face.idxGroups.length; i++) {
                IndexGroup group = this.idxGroups[i];
                face.idxGroups[i] = new IndexGroup(group.idxPos + v, group.idxTextCoord + t, group.idxVecNormal + n);
            }
            return face;
        }
    }

    public static class IndexGroup {
        public static final int NO_VALUE = -1;

        public int idxPos = -1;

        public int idxTextCoord = -1;

        public int idxVecNormal = -1;

        public IndexGroup(int v, int t, int n) {
            this.idxPos = v;
            this.idxTextCoord = t;
            this.idxVecNormal = n;
        }

        public IndexGroup() {
        }
    }

    public static class Vertex {
        public float x;

        public float y;

        public float z;

        public List<Weight> weights = new ArrayList<>();

        public Vertex(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void eliminateTinyWeights() {
            Iterator<Weight> it = this.weights.iterator();
            while (it.hasNext()) {
                Weight w = it.next();
                if (w.factor < 0.05D)
                    it.remove();
            }
            if (this.weights.size() > 0) {
                float weight = 0.0F;
                for (Weight w : this.weights)
                    weight += w.factor;
                if (weight < 1.0F)
                    ((Weight) this.weights.get(this.weights.size() - 1)).factor += 1.0F - weight;
            }
        }
    }

    public static class Weight {
        public String name;

        public float factor;

        public Weight(String name, float factor) {
            this.name = name;
            this.factor = factor;
        }
    }

    public static class BOBJData {
        public List<Vertex> vertices;

        public List<Vector2f> textures;

        public List<Vector3f> normals;

        public List<BOBJMesh> meshes;

        public Map<String, BOBJAction> actions;

        public Map<String, BOBJArmature> armatures;

        public BOBJData(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals, List<BOBJMesh> meshes, Map<String, BOBJAction> actions, Map<String, BOBJArmature> armatures) {
            this.vertices = vertices;
            this.textures = textures;
            this.normals = normals;
            this.meshes = meshes;
            this.actions = actions;
            this.armatures = armatures;
            for (BOBJMesh mesh : meshes)
                mesh.armature = armatures.get(mesh.armatureName);
        }

        public boolean hasGeometry() {
            return !this.meshes.isEmpty();
        }

        public void dispose() {
            this.vertices.clear();
            this.textures.clear();
            this.normals.clear();
            this.meshes.clear();
        }

        public void initiateArmatures() {
            for (BOBJArmature armature : this.armatures.values())
                armature.initArmature();
        }
    }

    public static class CompiledData {
        public float[] posData;

        public float[] texData;

        public float[] normData;

        public float[] weightData;

        public int[] boneIndexData;

        public int[] indexData;

        public BOBJMesh mesh;

        public CompiledData(float[] posData, float[] texData, float[] normData, float[] weightData, int[] boneIndexData, int[] indexData, BOBJMesh mesh) {
            this.posData = posData;
            this.texData = texData;
            this.normData = normData;
            this.weightData = weightData;
            this.boneIndexData = boneIndexData;
            this.indexData = indexData;
            this.mesh = mesh;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\bobj\BOBJLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */