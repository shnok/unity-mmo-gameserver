using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PlayerStateBase : StateMachineBehaviour
{
    public PlayerController pc;
	public Animator _animator;
	public NetworkTransform _network;

    public void SetPlayerController(Animator animator) {
        if(!pc)
            pc = animator.GetComponentInParent<PlayerController>();
		if(!_animator)
			_animator = animator;
		if(!_network)
			_network = animator.GetComponentInParent<NetworkTransform>();
    }

	public void SetBool(string name, bool value) {	
		if(_animator.GetBool(name) != value) {
			_animator.SetBool(name, value);

			EmitAnimatorInfo(name, value ? 1 : 0);
		}
	}

	public void SetFloat(string name, float value) {
		if(Mathf.Abs(_animator.GetFloat(name) - value) > 0.2f) {
			_animator.SetFloat(name, value);

			EmitAnimatorInfo(name, value);
		}
	}

	public void SetInteger(string name, int value) {
		if(_animator.GetInteger(name) != value) {
			_animator.SetInteger(name, value);

			EmitAnimatorInfo(name, value);
		}
	}

	public void SetTrigger(string name) {
		_animator.SetTrigger(name);

		EmitAnimatorInfo(name, 0);
	}

	private int SerializeAnimatorInfo(string name) {
		List<AnimatorControllerParameter> parameters  = new List<AnimatorControllerParameter>(_animator.parameters);

		int index = parameters.FindIndex(a => a.name == name);

		return index;
	}

	private void EmitAnimatorInfo(string name, float value) {
		if(!_network)
			return;

		if(_network.GetIdentity().owned) {
			int index = SerializeAnimatorInfo(name);
			if(index != -1) {
				_network.ShareAnimation(index, value);
            }
        }
	}
}
