package com.basti_bob.sand_wizard.input;

import com.badlogic.gdx.Input;

public interface InputElement {

     boolean keyDown (int keycode);

     boolean keyUp (int keycode);

     boolean keyTyped (char character);

     boolean touchDown (int screenX, int screenY, int pointer, int button);

     boolean touchUp (int screenX, int screenY, int pointer, int button);

     boolean touchCancelled (int screenX, int screenY, int pointer, int button);

     boolean touchDragged (int screenX, int screenY, int pointer);

     boolean mouseMoved (int screenX, int screenY);

     boolean scrolled (float amountX, float amountY);

     boolean shouldListenToInput();
}
