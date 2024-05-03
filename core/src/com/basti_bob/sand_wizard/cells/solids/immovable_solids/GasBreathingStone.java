package com.basti_bob.sand_wizard.cells.solids.immovable_solids;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.gases.Gas;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.util.range.FloatRange;
import com.basti_bob.sand_wizard.util.range.IntRange;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public class GasBreathingStone extends ImmovableSolid {

    private final CellType toCreateGas;
    private final IntRange intervalRange;
    private final IntRange activeTimeRange;
    private final int amount;
    private final FloatRange angleRange;
    private final FloatRange velRange;
    private final IntRange lifeTimeRange;

    private final int centerOff;

    private int activeTime = 0;
    private int timeUntilActive;

    public GasBreathingStone(CellType cellType, CellType toCreateGas, IntRange intervalRange, IntRange activeTimeRange, int amount,
                             FloatRange angleRange, FloatRange velRange, IntRange lifeTimeRange) {
        super(cellType);
        this.toCreateGas = toCreateGas;
        this.intervalRange = intervalRange;
        this.activeTimeRange = activeTimeRange;
        this.amount = amount;
        this.angleRange = angleRange;
        this.velRange = velRange;
        this.lifeTimeRange = lifeTimeRange;

        float angleFactor = (Math.abs(angleRange.max - angleRange.min) % MathUtil.TWO_PI) / MathUtil.TWO_PI;

        this.centerOff = (int) Math.ceil(amount / angleFactor / 6f) + 1;
    }

    @Override
    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        super.update(chunkAccessor, updateDirection);

        timeUntilActive--;
        if (timeUntilActive < 0) {

            breatheGas();

            if (timeUntilActive < -activeTime) {
                timeUntilActive = intervalRange.getRandom(world.random);
                activeTime = activeTimeRange.getRandom(world.random);
            }
        }
    }

    public void breatheGas() {

        for (int i = 0; i < amount; i++) {
            float a = MathUtil.map(i, 0, amount - 1, angleRange.min, angleRange.max);
            float r = velRange.getRandom(world.random);

            float c = (float) Math.cos(a);
            float s = (float) Math.sin(a);

            int fireX = (int) (this.getPosX() + c * centerOff);
            int fireY = (int) (this.getPosY() + s * centerOff);

            Gas gas = (Gas) toCreateGas.createCell();
            gas.velocityX = r * c;
            gas.velocityY = r * s;
            gas.lifeTime = lifeTimeRange.getRandom(world.random);
            world.setCell(gas, fireX, fireY);
        }
    }

    @Override
    public boolean shouldActiveChunk() {
        return true;
    }
}
