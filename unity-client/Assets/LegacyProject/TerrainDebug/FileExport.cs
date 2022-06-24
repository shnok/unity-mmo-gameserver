using System.Collections;
using System.Collections.Generic;
using System.IO;
using UnityEngine;

public class FileExport {
    public FileExport(){
    }

    public void Export(string filename, Dictionary<Vector3, Node> terrainData) {
        using(BinaryWriter binWriter = new BinaryWriter(File.Open(filename, FileMode.Create))) {
            int writeCount = 0;
            foreach(KeyValuePair<Vector3, Node> entry in terrainData) {
                Debug.Log(entry.Key + " : " + Terrain.flatten((int)entry.Key.x, (int)entry.Key.y, (int)entry.Key.z));
                binWriter.Write(Terrain.flatten((int)entry.Key.x, (int)entry.Key.y, (int)entry.Key.z));
                binWriter.Write(entry.Value.walkable ? (byte)1: (byte)0);
                writeCount++;
            }
            Debug.Log(writeCount);
        }      
    }
}
