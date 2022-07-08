using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Diagnostics;

public class Terrain : MonoBehaviour {

	public static Dictionary<Vector3, Node> terrain = new Dictionary<Vector3, Node>();
	public static int WORLD_SIZE = 18;
	public static int WORLD_HEIGHT = 18;
	public bool drawGizmos = true;
	public LayerMask collisionMask;
	public static LayerMask mask;
	public int terrainSize = 18;
	public LayerMask walkableMask;

	// Use this for initialization
	void Start () {
		Stopwatch s = new Stopwatch ();
		s.Start ();
		CreateTerrain ();
		UnityEngine.Debug.Log ("Built the terrain in " + s.ElapsedMilliseconds + " ms");
		s.Stop ();
		mask = collisionMask;

		FileExport fe = new FileExport();
		fe.Export("F:\\Stock\\Projects\\unity-mmo\\java-server\\src\\main\\resources\\terrain.dat", terrain);
	}

	void CreateTerrain() {

		Vector3 size = new Vector3 (0.45f, 0.1f, 0.45f);

		for (int x = -terrainSize; x < terrainSize; x++) {
			for (int z = -terrainSize; z < terrainSize; z++) {

				float oldHeight = 0;

				RaycastHit[] hits = Physics.BoxCastAll (new Vector3 (x + 0.5f, 50, z + 0.5f)
				                    + transform.position, size, -Vector3.up, Quaternion.identity, 100f, walkableMask);
				
				UnityEngine.Profiling.Profiler.BeginSample ("Calc");
				hits = hits.OrderBy(h=>h.distance).ToArray();
				UnityEngine.Profiling.Profiler.EndSample ();

				for(int i = 0; i < hits.Length ; i++) {
					
					if (i == 0)
						oldHeight = hits [i].point.y;
					
					//Vector3 nodePos = new Vector3 (Mathf.Floor (transform.position.x) + x, Mathf.Round(hits[i].point.y * 10) / 10, Mathf.Floor (transform.position.z) + z);
					Vector3 nodePos = new Vector3 (Mathf.Floor (transform.position.x) + x, Mathf.Floor(hits[i].point.y), Mathf.Floor (transform.position.z) + z);

					//UnityEngine.Debug.Log(nodePos);

					if((oldHeight - hits[i].point.y) > 1.5f || i == 0) {
						Node n = new Node (nodePos, 0, 0);

						terrain.Add (nodePos, n);

						n.CalculateNodeStatus (collisionMask);

						oldHeight = hits [i].point.y;
					}
				}
			}
		}
	}

	public static int flatten(int x, int y, int z) {
		return (y * Terrain.WORLD_HEIGHT * 2 * Terrain.WORLD_SIZE * 2) + (z * Terrain.WORLD_SIZE * 2) + x;
	}

	/*public static Node GetNode(Vector3 pos) {

		pos.Set(Mathf.Floor (pos.x),  Mathf.Floor (pos.y),  Mathf.Floor (pos.z));

		for (int i = -25; i <= 15; i++) { 
			Node n;
			if (terrain.TryGetValue (pos + Vector3.up * (float)i/10, out n)) {
				return n;
			}
		}

		return null;
	}*/

	/*public static Node[] GetNodeNeighbors(Node n) {

		Node[] returnList = new Node[8];

		int i = 0;
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (x == 0 && z == 0)
					continue;

				Node node = GetNode (new Vector3 (x, 0, z) + n.position);

				if(node != null)
					returnList[i] = (node);

				i++;
			}

		}

		return returnList;
	}*/



	void OnDrawGizmos() {

		if (!drawGizmos)
			return;

		if (terrain.Count > 0) {
			//Debug.Log ("allo");
			foreach (KeyValuePair<Vector3, Node> n in terrain) {
				Vector3 cubeSize = new Vector3 (0.95f, 0.1f, 0.95f);
				if (n.Value.walkable) {
					Gizmos.color = Color.green;
				} else {
					Gizmos.color = Color.red;
				}

				Gizmos.DrawCube (n.Value.center, cubeSize);
			}
		}

	}
}
