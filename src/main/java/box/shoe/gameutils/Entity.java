package box.shoe.gameutils;

import android.graphics.RectF;
import android.support.annotation.CallSuper;
import android.support.annotation.RestrictTo;
import android.util.Log;

import java.util.Collection;
import java.util.HashSet;

import box.gift.gameutils.BuildConfig;

/**
 * Created by Joseph on 12/9/2017.
 * A game object which occupies position and space in the game and can move around.
 * An Entity is not necessarily fit for rendering.
 * @see Renderable for an Entity which is meant to be displayed during the game.
 */ //TODO: type of short-lived entity that exists only for a number of frames? (particle)
public class Entity implements Updatable, Interpolatable /* Poolable*/
{
    // The game-space which is occupied by this Entity.
    public AABB body;
    // The screen-space which is occupied by this Entity.
    public AABB display;

    // Vector which represents how many x and y units the body will offset by per update.
    public Vector velocity;
    // Vector which represents how many x and y units the velocity will change by per update.
    public Vector acceleration;

    // Entities can have children, who stick to their parent.
    private Collection<Entity> children;

    // Enforce cleanup method call.
    private boolean cleaned = false;

    public Entity(RectF body)
    {
        this(body, Vector.ZERO, Vector.ZERO);
    }

    public Entity(RectF body, Vector initialVelocity)
    {
        this(body, initialVelocity, Vector.ZERO);
    }

    /**
     * Creates an Entity with ....
     * //todo: complete
     * @param initialVelocity the starting velocity.
     * @param initialAcceleration the starting acceleration.
     */
    public Entity(RectF body, Vector initialVelocity, Vector initialAcceleration)
    {
        // Do some dimension checks. Removing these checks could potentially be interesting,
        // but would probably not lead to behavior that is intended most of the time.
        if (body.width() < 0)
        {
            throw new IllegalArgumentException("Width cannot be less than 0: " + body.width());
        }
        if (body.height() < 0)
        {
            throw new IllegalArgumentException("Height cannot be less than 0: " + body.height());
        }
        this.body = new AABB(body);
        display = new AABB(body);
        velocity = initialVelocity;
        acceleration = initialAcceleration;
        children = new HashSet<>();
    }

    public void addChild(Entity child)
    {
        children.add(child);
    }

    public void removeChild(Entity child)
    {
        children.remove(child);
    }

    /**
     * Semi-Implicit Euler integration -
     * Updates velocity based on current acceleration,
     * and then updates position based on new velocity.
     * We do not need to multiply by dt because every time-step is of equal length.
     */
    @Override
    @CallSuper
    public void update()
    {
        float saveCenterX = body.centerX();
        float saveCenterY = body.centerY();

        // We will update velocity based on acceleration first,
        // and update position based on velocity second.
        // This is apparently called Semi-Implicit Euler and is a more accurate form of integration
        // when acceleration is not constant.

        // By default, acceleration is preserved through updates, but subclasses can change this behavior,
        // as can external code (acceleration is settable, after all).

        // Update velocity first based on current acceleration.
        updateVelocity();

        // Update position based on new velocity.
        updatePosition();

        // We do not update all children, but we do offset them by the amount of their parent.
        for (Entity child : children)
        {
            child.body.offset(body.centerX() - saveCenterX, body.centerY() - saveCenterY);
        }
    }

    /**
     * We define update() in terms of smaller functions so that subclasses have more customization options.
     * The purpose of this function is to update this Entity's velocity.
     * The default implementation is to add acceleration.
     */
    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    @CallSuper
    protected void updateVelocity()
    {
        velocity = velocity.add(acceleration);
    }

    /**
     * We define update() in terms of smaller functions so that subclasses have more customization options.
     * The purpose of this function is to update this Entity's position.
     * The default implementation is to offset by velocity.
     */
    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    @CallSuper
    protected void updatePosition()
    {
        body.offset(velocity.getX(), velocity.getY());
    }

    public Vector vectorTo(Entity other)
    {
        return Vector.fromCartesian(other.body.centerX() - body.centerX(), other.body.centerY() - body.centerY());
    }


    /**
     * Cleanup should always be called before an Entity is eligible to be Garbage Collected.
     * After a cleanup call, the Entity is no longer usable, and should be de-referenced immediately.
     * Any use of an Entity object or its aggregates after cleanup is called is undefined.
     * This should have no effect if called more than once for any particular Entity.
     * i.e. calls after the first one should be idempotent.
     * Turn on debug mode get warnings when cleanup was not called prior to an Entity being GC'd.
     */
    @CallSuper
    public void cleanup()
    {
        if (INTERPOLATABLE_SERVICE.hasMember(this))
        {
            INTERPOLATABLE_SERVICE.removeMember(this);
        }
        cleaned = true;
    }

    /**
     * When trying to debug, let the user know when they have de-referenced an Entity that was
     * not cleaned up. Not cleaning up an Entity can lead to sub-optimal performance
     * in the time before it is garbage collected, because they will still be in the
     * lists of services that will operate on them, without any real reason.
     * The services use weak data structures so the Entities may still be eventually GC'd,
     * at which point, this finalize may be run to alert the user that cleanup was not called.
     * Of course, it is up to each Entity subclass to override cleanup() to remove the services
     * they register for (and also to throw the Entity back in a Pool if it came from one).
     */
    @Override
    protected void finalize() throws Throwable
    {
        try
        {
            if (!cleaned && BuildConfig.DEBUG)
            {
                // We log a warning because throwing an error here will not stop program execution
                // anyway and there is technically no 'error' here. For all we know, the Entities do not
                // need to be cleaned up. This warning exists for debug purposes only.
                Log.w("Entity Finalizer", getClass().getName() + "|" + this + " was garbage collected before being cleaned! Try calling cleanup() on Entities that you are done with before de-referenceing them.");
            }
        }
        finally
        {
            super.finalize();
        }
    }

    // We mark this method as final to enforce all Collections of Entities to be identity collections.
    @Override
    public final boolean equals(Object other)
    {
        return super.equals(other);
    }

    // We mark this method as final to enforce all Collections of Entities to be identity collections.
    @Override
    public final int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public int getInterpValuesArrayMaxIndex()
    {
        return 3;
    }

    @Override
    public void saveInterpValues(float[] out)
    {
        out[0] = body.left;
        out[1] = body.top;
        out[2] = body.right;
        out[3] = body.bottom;
    }

    @Override
    public void loadInterpValues(float[] in)
    {
        display.left = in[0];
        display.top = in[1];
        display.right = in[2];
        display.bottom = in[3];
    }
}
