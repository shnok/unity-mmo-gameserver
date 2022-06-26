using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CombatController : MonoBehaviour
{
	/* Combat */
	public bool weaponOut;
	public Transform weapon;
	public Transform shield;
	public Transform holsterWeapons;

	void Start()
    {
		weaponOut = true;
    }

	public void HolsterWeapons() {
		if(weaponOut) {
			transform.GetComponentInChildren<Xft.XWeaponTrail>(true).Deactivate();
			weapon.gameObject.SetActive(false);
			shield.gameObject.SetActive(false);
			holsterWeapons.gameObject.SetActive(true);
		} else {
			weapon.gameObject.SetActive(true);
			shield.gameObject.SetActive(true);
			holsterWeapons.gameObject.SetActive(false);
		}

		weaponOut = !weaponOut;
	}

}
