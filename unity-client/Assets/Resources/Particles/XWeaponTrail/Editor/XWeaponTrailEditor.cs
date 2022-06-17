using UnityEngine;
using System.Collections;
using UnityEditor;
using Xft;

[CustomEditor(typeof(XWeaponTrail))]
[CanEditMultipleObjects]
public class XWeaponTrailEditor : Editor
{
    public override void OnInspectorGUI()
    {


        EditorGUILayout.BeginVertical();
        EditorGUILayout.LabelField("Version: " + XWeaponTrail.Version);
        EditorGUILayout.LabelField("Email: shallwaycn@gmail.com");
        //EditorGUILayout.LabelField("Web: http://phantomparticle.org");
        EditorGUILayout.EndVertical();

        GUI.color = Color.green;
        EditorGUILayout.LabelField("Please check out our new assets");
        if (GUILayout.Button("Phantom Particle", GUILayout.Height(32)))
        {
            Application.OpenURL("https://www.assetstore.unity3d.com/en/#!/content/56597");
        }
        GUI.color = Color.white;

        GUILayout.Space(10);
        //DrawDefaultInspector();

        SerializedProperty UseWith2D = serializedObject.FindProperty("UseWith2D");
        EditorGUILayout.PropertyField(UseWith2D);
        if (UseWith2D.boolValue) {
            EditorGUILayout.PropertyField(serializedObject.FindProperty("SortingLayerName"));
            EditorGUILayout.PropertyField(serializedObject.FindProperty("SortingOrder"));
        }

        EditorGUILayout.PropertyField(serializedObject.FindProperty("PointStart"));
        EditorGUILayout.PropertyField(serializedObject.FindProperty("PointEnd"));
        EditorGUILayout.PropertyField(serializedObject.FindProperty("MaxFrame"));
        EditorGUILayout.PropertyField(serializedObject.FindProperty("Granularity"));
        EditorGUILayout.PropertyField(serializedObject.FindProperty("Fps"));
        EditorGUILayout.PropertyField(serializedObject.FindProperty("MyColor"));
        EditorGUILayout.PropertyField(serializedObject.FindProperty("MyMaterial"));

        if (GUI.changed) {

            serializedObject.ApplyModifiedProperties();

            EditorUtility.SetDirty(target);
        }
    }
}

