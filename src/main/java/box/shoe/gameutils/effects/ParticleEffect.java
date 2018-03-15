package box.shoe.gameutils.effects;

import android.content.res.Resources;
import android.graphics.Canvas;

import java.util.Collection;
import java.util.LinkedList;

import box.shoe.gameutils.Entity;
import box.shoe.gameutils.Vector;

/**
 * Created by Joseph on 3/16/2018.
 */

public class ParticleEffect implements Effect
{
    private final int PARTICLE_DURATION;

    protected Collection<? extends Particle> particles;

    public ParticleEffect(int particleDuration)
    {
        PARTICLE_DURATION = particleDuration;
        particles = new LinkedList<>();
    }

    @Override
    public void produce()
    {

    }

    @Override
    public void update()
    {

    }

    @Override
    public void render(Resources resources, Canvas canvas)
    {

    }

    private static class Particle extends Entity
    {
        private int updatesLeft;

        public Particle(int duration, float initialX, float initialY, float initialWidth, float initialHeight, Vector initialVelocity, Vector initialAcceleration)
        {
            super(initialX, initialY, initialWidth, initialHeight, initialVelocity, initialAcceleration);
            updatesLeft = duration;
        }

        @Override
        public void update()
        {
            super.update();
            updatesLeft -= 1;
        }
    }
}