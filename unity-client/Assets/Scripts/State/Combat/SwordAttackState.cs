using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SwordAttackState : PlayerStateBase {
    public float start;
    public float elapsed;

    // OnStateEnter is called when a transition starts and the state machine starts to evaluate this state
    override public void OnStateEnter(Animator animator, AnimatorStateInfo stateInfo, int layerIndex) {
        animator.ResetTrigger("ExitAttack");
        animator.ResetTrigger("Attack");
        SetPlayerController(animator);
        start = Time.time;
        pc.canMove = false;
    }

    // OnStateUpdate is called on each Update frame between OnStateEnter and OnStateExit callbacks
    override public void OnStateUpdate(Animator animator, AnimatorStateInfo stateInfo, int layerIndex) {
        elapsed = Time.time - start;
        if(elapsed >= 0.3f) {
            if(Input.GetMouseButton(0)) {
                SetTrigger("Attack");
                if(!animator.GetNextAnimatorStateInfo(0).IsName("Attack1") && 
                    !animator.GetNextAnimatorStateInfo(0).IsName("Attack2") && 
                    !animator.GetNextAnimatorStateInfo(0).IsName("Attack3")) {
                    pc.LookForward("camera");
                }
            }
            if(elapsed >= 0.5f) {
                pc.canMove = true;
                if(elapsed >= 1f || animator.GetCurrentAnimatorStateInfo(0).IsName("Run")) {
                    SetTrigger("ExitAttack");
                }
                if(animator.GetNextAnimatorStateInfo(0).IsName("Jump")) {
                    SetTrigger("ForceExitAttack");
                }
            }
        } else {
            pc.canMove = false;
        }
    }

    // OnStateExit is called when a transition ends and the state machine finishes evaluating this state
    override public void OnStateExit(Animator animator, AnimatorStateInfo stateInfo, int layerIndex) {
        animator.ResetTrigger("Attack");
        animator.ResetTrigger("ExitAttack");
    }

    // OnStateMove is called right after Animator.OnAnimatorMove()
    override public void OnStateMove(Animator animator, AnimatorStateInfo stateInfo, int layerIndex) {
        //Implement code that processes and affects root motion
    }

    // OnStateIK is called right after Animator.OnAnimatorIK()
    override public void OnStateIK(Animator animator, AnimatorStateInfo stateInfo, int layerIndex) {
        // Implement code that sets up animation IK (inverse kinematics)
    }
}
