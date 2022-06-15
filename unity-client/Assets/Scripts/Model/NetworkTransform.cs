using UnityEngine;

public class NetworkTransform : MonoBehaviour {
    public NetworkIdentity identity;
    private Vector3 _lastPos, _newPos, _lastRotForward;
    private Quaternion _lastRot, _newRot;
    private float _rotLerpValue, _posLerpValue, _lastSharedPosTime, _lastSharedRotTime;
    private Animator _animator;

    public void SetIdentity(NetworkIdentity i) {
       identity = i; 
    }

    public NetworkIdentity GetIdentity() {
        return identity;
    }
    void Start() {
        _animator = GetComponent<Animator>();
        _lastPos = transform.position;
        _newPos = transform.position;
        _newRot = transform.rotation;
        _lastRot = transform.rotation;
        _lastSharedPosTime = Time.time;
        _lastSharedRotTime = Time.time;
    }

    void Update() {
        if(identity.owned) {
            SharePosition();
            ShareRotation();
        } else {
            LerpToPosition();
            LerpToRotation();
        }
    }

    public void SharePosition() {
        if(Vector3.Distance(transform.position, _lastPos) > .25f || Time.time - _lastSharedPosTime >= 1f) {
            _lastSharedPosTime = Time.time;
            ClientPacketHandler.GetInstance().UpdatePosition(transform.position);
            _lastPos = transform.position;
        }
    }

    public void ShareRotation() {
        if(Vector3.Angle(_lastRotForward, transform.forward) >= 15.0f || Time.time - _lastSharedRotTime >= 1f) {
            _lastSharedRotTime = Time.time;
            _lastRotForward = transform.forward;
            ClientPacketHandler.GetInstance().UpdateRotation(transform.eulerAngles.y);
        }
    }

    public void ShareAnimation(int id, float value) {
        Debug.Log("Share animation");
        ClientPacketHandler.GetInstance().UpdateAnimation(id, value);
    }

    public void LerpToPosition() {
        transform.position = Vector3.Lerp(_lastPos, _newPos, _posLerpValue);
        _posLerpValue += (1 / 0.10f) * Time.deltaTime;
    }

    public void LerpToRotation() {
        if(Mathf.Abs(transform.rotation.eulerAngles.y - _newRot.eulerAngles.y) > 2f) {
            transform.rotation = Quaternion.Lerp(_lastRot, _newRot, _rotLerpValue);
            _rotLerpValue += Time.deltaTime * 7.5f;
        }
    }

    public void MoveTo(Vector3 pos) {
        _newPos = pos;     
        _lastPos = transform.position;
        _posLerpValue = 0;
    }

    public void RotateTo(float angle) {
        Quaternion rot = transform.rotation;
        Vector3 eulerAngles = transform.rotation.eulerAngles;
        eulerAngles.y = angle;
        rot.eulerAngles = eulerAngles;

        _newRot = rot;
        _lastRot = transform.rotation;
        _rotLerpValue = 0;
    }

    public void SetAnimationProperty(int animId, float value) {
        if(animId > 0 && animId < _animator.parameters.Length) {
            AnimatorControllerParameter anim = _animator.parameters[animId];
            switch(anim.type) {
                case AnimatorControllerParameterType.Float:
                    _animator.SetFloat(anim.name, value);
                    break;
                case AnimatorControllerParameterType.Int:
                    _animator.SetInteger(anim.name, (int)value);
                    break;
                case AnimatorControllerParameterType.Bool:
                    _animator.SetBool(anim.name, (int)value == 1);
                    break;
                case AnimatorControllerParameterType.Trigger:
                    _animator.SetTrigger(anim.name);
                    break;
            }
        }     
    }
}