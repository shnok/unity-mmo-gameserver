using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class Label : MonoBehaviour {
	private bool _visible;
	private float _distance;
	private GameObject _target;

	public RectTransform _healthBar;
	public RectTransform _barContour;
	public Text _name;
	public Text _level;

    void Start() {
		_healthBar = transform.GetChild(0).Find("HealthBar").GetComponent<RectTransform>();
		_barContour = transform.GetChild(0).Find("Contour").GetComponent<RectTransform>();
		_level = transform.GetChild(0).Find("Level").GetComponent<Text>();
		_name = transform.GetChild(0).Find("Name").GetComponent<Text>();
	}

    void Update() {
		if(_target == null) {
			Destroy(gameObject);
		}

		_visible = Camera.main.GetComponent<CameraController>().IsVisible(_target);

		if(_visible) {
			transform.position = Camera.main.WorldToScreenPoint(_target.transform.position + Vector3.up * 1.25f);
			transform.GetChild(0).gameObject.SetActive(true);

			Status targetStatus = _target.GetComponent<Entity>().status;
			float hpPercent = (float)targetStatus.Hp / (float)targetStatus.MaxHp;
			hpPercent = Mathf.Clamp(hpPercent, 0.0f, 1.0f);
			_healthBar.sizeDelta = new Vector2(hpPercent * _barContour.sizeDelta.x, _healthBar.sizeDelta.y);
			_level.text = targetStatus.Level.ToString();
			_name.text = _target.transform.name;
		} else {
			transform.GetChild(0).gameObject.SetActive(false);
		}
	}

	public void SetTarget(GameObject target) {
		_target = target;
	}

}
