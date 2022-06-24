using UnityEngine;
using System.Collections;

public class EntityAnimationOld : MonoBehaviour {
#if false
	[HideInInspector]
	public Animator animator;
	public EntityStatus status;
	public NetworkSyncAnim syncAnim;

	// Use this for initialization
	void Start () {
		status = GetComponent<EntityStatus> ();
		syncAnim = GetComponent<NetworkSyncAnim> ();
		animator = GetComponent<Animator>();

		if(animator== null) {
			animator = GetComponentInChildren<Animator> ();
		}
	}
	
	// Update is called once per frame
	void Update () {
		if(status.state == MonsterState.Runing) {
			SetBool ("Run", true);
		} else {
			SetBool ("Run", false);
		}

		if(status.state == MonsterState.Waiting) {
			SetBool ("Wait", true);
		} else {
			SetBool ("Wait", false);
		}

		if(status.state == MonsterState.Walking) {
			SetBool ("Walk", true);
		} else {
			SetBool ("Walk", false);
		}

		if(status.state == MonsterState.Attacking) {
			SetBool ("Attack", true);
		} else {
			SetBool ("Attack", false);
		}

		if(status.state == MonsterState.Dead) {
			SetBool ("Dead", true);
		} else {
			SetBool ("Dead", false);
		}
	}

	void SetBool(string name, bool value) {
		if (animator.GetBool (name) != value) {
			animator.SetBool (name, value);
			syncAnim.EmitAnimatorInfo (name, value.ToString());
		}
	}

	void SetFloat(string name, float value) {
		if (Mathf.Abs(animator.GetFloat (name) - value) > 0.2f) {
			animator.SetFloat (name, value);
			syncAnim.EmitAnimatorInfo (name, value.ToString());
		}
	}
#endif
}
