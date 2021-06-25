using UnityEngine;

public class NetworkTransform : MonoBehaviour {
    public NetworkIdentity identity; 
    private Vector3 _lastPos, _newPos;
    private float _newRot;
    private float _lerpPos;

    public void SetIdentity(NetworkIdentity i) {
       identity = i; 
    }

    public NetworkIdentity GetIdentity() {
        return identity;
    }
    void Start() {
        _lastPos = transform.position;
        _newPos = transform.position;
        _newRot = transform.eulerAngles.y;
    }

    void Update() {
        if(identity.owned) {
            SharePosition();
        } else {
            LerpToPosition();
            LerpToRotation();
        }
    }

    public void SharePosition() {
        if(Vector3.Distance(transform.position, _lastPos) > .25f) {
            ClientPacketHandler.GetInstance().UpdatePosition(transform.position);
            _lastPos = transform.position;
        }
    }

    public void ShareRotation(float angle) {
        ClientPacketHandler.GetInstance().UpdateRotation(angle);
    }

    public void LerpToPosition() {
        if(Vector3.Distance(transform.position, _newPos) > 0.05f) {
            transform.position = Vector3.Lerp (_lastPos, _newPos, _lerpPos);
            _lerpPos += (1 / 0.10f) * Time.deltaTime;
        }
    }

    public void LerpToRotation() {
        transform.rotation = Quaternion.Lerp (transform.rotation, Quaternion.Euler (Vector3.up * _newRot), Time.deltaTime * 7.5f);
    }

    public void MoveTo(Vector3 pos) {
        _newPos = pos;     
        _lastPos = transform.position;
        _lerpPos = 0;  
    }

    public void RotateTo(float angle) {
        _newRot = angle;
    }
}