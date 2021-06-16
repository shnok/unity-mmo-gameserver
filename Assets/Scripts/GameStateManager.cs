using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public enum GameState { MENU, CONNECTED }
public delegate void OnStateChangeHandler();

public class GameStateManager : MonoBehaviour
{
    public static event OnStateChangeHandler StateChanged;
    private static GameState _state;
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
        offlineMenu.SetActive(_state == GameState.MENU);
        onlineMenu.SetActive(_state == GameState.CONNECTED);
    }
}
