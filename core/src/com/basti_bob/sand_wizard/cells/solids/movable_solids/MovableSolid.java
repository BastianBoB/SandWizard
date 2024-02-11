package com.basti_bob.sand_wizard.cells.solids.movable_solids;

import com.badlogic.gdx.math.MathUtils;
import com.basti_bob.sand_wizard.coordinateSystems.CellPos;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.cells.solids.Solid;

public abstract class MovableSolid extends Solid {

    private boolean moving;

    public MovableSolid(World world, int x, int y) {
        super(world, x, y);
    }

    public float getMovingResistance() {
        return 0;
    }

    public void setMoving() {
        this.moving = this.moving || MathUtils.random() > this.getMovingResistance();
    }

    @Override
    public void update() {
        super.update();

        boolean spaceBelow = canMoveToOrSwap(x, y+1);

        if (spaceBelow) {
            //this.velocity.add(this.getGravity());

            this.moveTo(below); // + (int) this.velocity.y);
        } else {
            this.velocity.y = -1;

            if (canMoveToOrSwap(new CellPos(x + 1, y - 1))) {
                this.moveTo(new CellPos(x + 1, y - 1));
            } else if (canMoveToOrSwap(new CellPos(x - 1, y - 1))) {
                this.moveTo(new CellPos(x - 1, y - 1));
            }
        }
    }

}
