using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PlayerStateBase : StateMachineBehaviour
{
    public PlayerController pc;
	public Animator _animator;

    public void SetPlayerController(Animator animator) {
        if(!pc)
            pc = animator.GetComponentInParent<PlayerController>();
		if(!_animator)
			_animator = animator;
    }

	public void SetBool(string name, bool value) {
		if(_animator.GetBool(name) != value) {
			_animator.SetBool(name, value);
			//if(!local)
			//	syncAnim.EmitAnimatorInfo (name, value.ToString());
		}
	}

	public void SetFloat(string name, float value) {
		if(Mathf.Abs(_animator.GetFloat(name) - value) > 0.2f) {
			_animator.SetFloat(name, value);
			//	if (!local) 
			//	syncAnim.EmitAnimatorInfo (name, (Mathf.Floor((value+0.01f)*100)/100).ToString ());
		}
	}

	public void SetInteger(string name, int value) {
		if(_animator.GetInteger(name) != value) {
			_animator.SetInteger(name, value);
			//if (!local) 
			//	syncAnim.EmitAnimatorInfo (name, value.ToString ());
		}
	}

	public void SetTrigger(string name) {
		_animator.SetTrigger(name);
		//if (!local)
		//	syncAnim.EmitAnimatorInfo (name, "");
	}
}
