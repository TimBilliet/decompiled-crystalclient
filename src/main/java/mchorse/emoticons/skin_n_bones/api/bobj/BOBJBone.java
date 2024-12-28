package mchorse.emoticons.skin_n_bones.api.bobj;

import co.crystaldev.client.util.javax.Matrix4f;
import co.crystaldev.client.util.javax.Tuple3f;
import co.crystaldev.client.util.javax.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BOBJBone {
  public int index;
  
  public String name;
  
  public String parent;
  
  public BOBJBone parentBone;
  
  public List<BOBJBoneModifier> modifiers;
  
  public Vector3f head;
  
  public Vector3f tail;
  
  public float length;
  
  public float x;
  
  public float y;
  
  public float z;
  
  public float rotateX;
  
  public float rotateY;
  
  public float rotateZ;
  
  public float scaleX = 1.0F;
  
  public float scaleY = 1.0F;
  
  public float scaleZ = 1.0F;
  
  public Matrix4f mat = new Matrix4f();
  
  public Matrix4f boneMat;
  
  public Matrix4f invBoneMat = new Matrix4f();
  
  public Matrix4f relBoneMat = new Matrix4f();
  
  public Matrix4f tempMat = new Matrix4f();
  
  public BOBJBone(int index, String name, String parent, Vector3f tail, Matrix4f boneMat) {
    this.index = index;
    this.name = name;
    this.parent = parent;
    this.boneMat = boneMat;
    this.head = new Vector3f(boneMat.m03, boneMat.m13, boneMat.m23);
    this.tail = tail;
    Vector3f diff = new Vector3f(this.tail);
    diff.sub((Tuple3f)this.head);
    this.length = diff.length();
    this.invBoneMat.set(boneMat);
    this.invBoneMat.invert();
    this.relBoneMat.setIdentity();
  }
  
  public void addModifier(BOBJBoneModifier modifier) {
    if (this.modifiers == null)
      this.modifiers = new ArrayList<>(); 
    this.modifiers.add(modifier);
  }
  
  public Matrix4f compute() {
    Matrix4f mat = computeMatrix(new Matrix4f());
    this.mat.set(mat);
    applyModifiers();
    mat.set(this.mat);
    mat.mul(this.invBoneMat);
    return mat;
  }
  
  public boolean hasModifiers() {
    return (this.modifiers != null);
  }
  
  public void applyModifiers() {
    if (hasModifiers())
      for (BOBJBoneModifier modifier : this.modifiers)
        modifier.apply(this);  
  }
  
  public Matrix4f computeMatrix(Matrix4f m) {
    m.setIdentity();
    this.mat.set(this.relBoneMat);
    applyTransformations();
    if (this.parentBone != null)
      m = new Matrix4f(this.parentBone.mat); 
    m.mul(this.mat);
    return m;
  }
  
  public void applyTransformations() {
    this.tempMat.setIdentity();
    this.tempMat.m03 = this.x;
    this.tempMat.m13 = this.y;
    this.tempMat.m23 = this.z;
    this.mat.mul(this.tempMat);
    this.tempMat.setIdentity();
    this.tempMat.m00 = this.scaleX;
    this.tempMat.m11 = this.scaleY;
    this.tempMat.m22 = this.scaleZ;
    this.mat.mul(this.tempMat);
    if (!hasModifiers()) {
      if (this.rotateZ != 0.0F) {
        this.tempMat.rotZ(this.rotateZ);
        this.mat.mul(this.tempMat);
      } 
      if (this.rotateY != 0.0F) {
        this.tempMat.rotY(this.rotateY);
        this.mat.mul(this.tempMat);
      } 
      if (this.rotateX != 0.0F) {
        this.tempMat.rotX(this.rotateX);
        this.mat.mul(this.tempMat);
      } 
    } 
  }
  
  public void reset() {
    this.x = this.y = this.z = 0.0F;
    this.rotateX = this.rotateY = this.rotateZ = 0.0F;
    this.scaleX = this.scaleY = this.scaleZ = 1.0F;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\bobj\BOBJBone.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */