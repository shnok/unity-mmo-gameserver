using UnityEngine;
using System.Collections;

public class PlayerController : MonoBehaviour {

	/* Components */
	private CharacterController controller;

	/*Rotate*/
	private float _finalAngle;

	/* Move */
	public Vector3 moveDirection;
	private float _currentSpeed;
	public float defaultSpeed = 6;

	/* Gravity */ 
	private float _verticalVelocity = 0;
	private float _jumpForce = 12;
	private float _gravity = 28;
    public Vector2 input;

	void Start () {
		controller = GetComponent<CharacterController> ();
	}
		
    void Update() {
        input = new Vector2(Input.GetAxisRaw ("Horizontal"), Input.GetAxisRaw ("Vertical"));
    }

	void FixedUpdate () {
        /* Speed */
        _currentSpeed = GetMoveSpeed (_currentSpeed);

        /* Angle */
		_finalAngle = GetRotationValue (_finalAngle);
        transform.rotation = Quaternion.Lerp (transform.rotation, Quaternion.Euler (Vector3.up * _finalAngle), Time.deltaTime * 7.5f);

        /* Direction */
		moveDirection = GetMoveDirection (moveDirection, _currentSpeed);
        controller.Move (moveDirection * Time.deltaTime);
	}

	float GetRotationValue(float angle) {
		if (KeyPressed()) {
            angle = Mathf.Atan2 (input.x, input.y) * Mathf.Rad2Deg;
            angle = Mathf.Round (angle / 45f);
            angle *= 45f;
            angle += Camera.main.transform.eulerAngles.y;
		}	

        return angle;	
	}

	Vector3 GetMoveDirection(Vector3 direction, float speed) {
        Vector3 forward = Camera.main.transform.TransformDirection (Vector3.forward);
        forward.y = 0;
        Vector3 right = new Vector3 (forward.z, 0, -forward.x);

		if (controller.isGrounded) {
			_verticalVelocity = -1.25f; 	
		} else {
			_verticalVelocity -= _gravity * Time.deltaTime; 
		}

		if (KeyPressed ()) {
			direction = input.x * right + input.y * forward;
		}

		direction = direction.normalized * speed;
		direction.y = _verticalVelocity;	

        return direction;
	}

	float GetMoveSpeed(float speed) {
		float smoothDuration = 0.2f;

		if(KeyPressed()) {
			speed = defaultSpeed;
		} else if(speed > 0 && controller.isGrounded) {
			speed -= (defaultSpeed/smoothDuration) * Time.deltaTime;
		}

		if(speed < 0) {
			speed = 0;
		}

        return speed;
	}

	public bool KeyPressed() {
        return input.x != 0 || input.y != 0;
	}
}
