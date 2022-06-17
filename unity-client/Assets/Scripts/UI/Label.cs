using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class Label : MonoBehaviour {

#if false
	public bool isVisible;
	public float viewDistance = 15f;

	public Transform target;

	public float height;
	private Camera camera;

	private NetworkTransform nt;
	private NetworkManager nm;

	void Start() {
		nm = GameObject.Find ("Network").GetComponent<NetworkManager> ();
		camera = Camera.main;
	}
		
	void Update() {
		CheckState ();
	}


	private void CheckState() {

		if(target == null) {
			Destroy (this.gameObject);
			return;
		}
			
		Vector3 viewPort = camera.WorldToViewportPoint (target.position);

		bool insideView = viewPort.x <= 1 && viewPort.x >= 0 && viewPort.y <= 1 && viewPort.y >= 0 && viewPort.z >= -0.2f;
		bool inRange = Vector3.Distance (target.position, nm.user.transform.position) < viewDistance;

		isVisible = insideView && inRange;

		if(isVisible) {
			if (!GetComponent<Text>().enabled) {
				transform.position = camera.WorldToScreenPoint (target.position + Vector3.up * height);;
				GetComponent<Text>().enabled = true;
			}
		} else {
			GetComponent<Text>().enabled = false;
		}

	}


	void LateUpdate() {
		if(isVisible && target!=null) {
			transform.position = camera.WorldToScreenPoint (target.position + Vector3.up * height);
		}
	}
#endif 

}
