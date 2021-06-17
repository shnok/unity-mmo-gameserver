using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public enum GameState { MENU, CONNECTED }
public delegate void OnStateChangeHandler();

public class GameStateManager : MonoBehaviour
{
    public static event OnStateChangeHandler StateChanged;
    private static GameState _state = GameState.MENU;
    private EventProcessor _eventProcessor;

    public GameObject offlineMenu;
    public GameObject onlineMenu;

    public static void SetState(GameState ns) {
        _state = ns;
        StateChanged?.Invoke();
    }

    public static GameState GetState() {
        return _state;
    }

    void Awake() {
        _eventProcessor = gameObject.GetComponent<EventProcessor>();
        StateChanged += HandleEvent;
    }

    private void HandleEvent() {
        _eventProcessor.QueueEvent(HandleOnStateChange);
    }

    private void HandleOnStateChange() {
        if(_state == GameState.MENU) {
            SceneManager.LoadSceneAsync("MenuScene");
        }
        if(_state == GameState.CONNECTED) {
            SceneManager.LoadSceneAsync("GameScene");
        }
    }
}
