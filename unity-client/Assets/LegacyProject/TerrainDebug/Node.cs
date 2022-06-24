using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System;

[System.Serializable]
public class Node  : ICloneable{

	public bool walkable;
	public Vector3 position;
	public Vector3 center;
	public int gridX, gridY;

	int heapIndex;

	//g price //h distance //f price
	public float g = 0, h = 0, f = 0;
	public Node parentNode;

	public Node(Vector3 position, int x, int y) {
		this.gridX = x;
		this.gridY = y;
		this.position = position;
		this.center = new Vector3 (position.x + 0.5f, position.y, position.z + 0.5f);
	}

	public object Clone()
	{
		return this.MemberwiseClone();
	}

	public void CalculateNodeStatus(LayerMask collisionMask) {

		/* Erosion */
		if(CheckErosion())
			Terrain.terrain.Remove (this.position);

		/* Covered */
		if(!CheckObstacle(collisionMask)) {
			walkable = true;
		}
			
	}

	private bool CheckErosion() {

		RaycastHit hit;
		for (int x = 0; x <= 1; x++) {
			for (int z = 0; z <= 1; z++) {
				if (!Physics.Raycast (position + new Vector3(x, 0, z) + Vector3.up, Vector3.down, out hit, 1.75f)) {
					return true;
				}
			}
		}
		return false;
	}

	private bool CheckObstacle(LayerMask mask) {
		Vector3 size = new Vector3 (0.45f, 0.1f, 0.45f);
		float height = 1.5f;

		if (Physics.BoxCast (center - Vector3.up, size, Vector3.up, Quaternion.identity, height, mask))  {
			return true;
		} else { 
			return false;
		}
	}


	public int HeapIndex {
		get {
			return heapIndex;
		}
		set {
			heapIndex = value;
		}
	}

	public int CompareTo(Node nodeToCompare) {
		int compare = f.CompareTo(nodeToCompare.f);
		if (compare == 0) {
			compare = h.CompareTo(nodeToCompare.h);
		}
		return -compare;
	}
}
