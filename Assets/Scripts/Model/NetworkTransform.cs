using UnityEngine;

public class NetworkTransform : MonoBehaviour {
    public NetworkIdentity identity; 
    public Vector3 lastPos;
    public Vector3 newPos;
    float lerp;
    public void SetIdentity(NetworkIdentity i) {
       identity = i; 
    }

    public NetworkIdentity GetIdentity() {
        return identity;
    }
    void Start() {
        lastPos = transform.position;
    }

    void Update() {
        if(identity.owned) {
            SendNewPosition();
        } else {
            LerpToPosition();
        }
    }

    public void SendNewPosition() {
        if(Vector3.Distance(transform.position, lastPos) > .25f) {
            Debug.Log("moved");
            ClientPacketHandler.GetInstance().UpdatePosition(transform.position);
            lastPos = transform.position;
        }
    }

    public void LerpToPosition() {
        if(Vector3.Distance(transform.position, newPos) > 0.1f) {
            transform.position = Vector3.Lerp (lastPos, newPos, lerp);
            lerp += (1 / 0.15f) * Time.deltaTime;
        }
    }

    public void MoveTo(Vector3 pos) {
        newPos = pos;     
        lastPos = transform.position;
        lerp = 0;  
    }
}