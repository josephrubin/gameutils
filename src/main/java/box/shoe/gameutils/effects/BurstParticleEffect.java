package box.shoe.gameutils.effects; //TODO: bad package for this to be in.

/**
 * Created by Joseph on 2/21/2018.
 * @deprecated
 */

//todo: needs to conform to Updatable/Renderable, and redo how we handle emitters and effects.
public class BurstParticleEffect //TODO: BurstEffect? or will there be separate, meaningfull classification for particle effects?
{/*
    private EntityEmitter emitter;

    private Paint paint;
    private HashMap<Entity, Integer> particles;
    private float size;
    private double speed;
    private int particleCount;
    private double startRadians;
    private double endRadians;
    private int duration;

    private BurstParticleEffect(float size, double speed, int color, int particleCount, double startRadians, double endRadians, int duration)
    {
        this.size = size;
        this.speed = speed;
        this.startRadians = startRadians;
        this.endRadians = endRadians;
        this.duration = duration;
        this.particleCount = particleCount;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        particles = new HashMap<>();

        emitter = new EntityEmitter(new BurstParticleEffectDisplayEntityFactory());
    }

    public void produce(float x, float y) //TODO: these methods should be in an interface, and maybe renamed.
    {
        emitter.setX(x);
        emitter.setY(y);

        for (int i = 0; i < particleCount; i++) //TODO: particleCount should be settable in builder
        {
            Entity particle = emitter.emit();//new DisplayEntity(x - size / 2, y - size / 2, size, size);
            particle.body.inset(- size / 2, - size / 2);
            particle.display.set(particle.body); //TODO: we still must decide whose responsibility this is. or if we do not paint on first frame or interpolation service.

            particle.velocity = Vector.fromPolar(speed, Math.toRadians(Rng.intFrom((int) Math.toDegrees(startRadians), (int) Math.toDegrees(endRadians)))); //TODO: we are turning deg into rad then back then forth. find better way (priority=low)
            particles.put(particle, 0);
        }
    }

    public void update()
    {
        Iterator<Map.Entry<Entity, Integer>> entryIterator = particles.entrySet().iterator();
        while (entryIterator.hasNext())
        {
            Map.Entry<Entity, Integer> entry = entryIterator.next();
            if (entry.getValue() >= duration)
            {
                entry.getKey().cleanup();
                entryIterator.remove();
            }
            else
            {
                entry.getKey().update();
                entry.setValue(entry.getValue() + 1);
            }
        }
    }

    public void paint(Canvas canvas, Resources resources)
    {
        for (Entity particle : particles.keySet())
        {
            canvas.drawRect(particle.display, paint);
        }
    }

    private static class BurstParticleEffectDisplayEntityFactory implements Entity.Factory
    {
        @Override
        public Entity create()
        {
            return new Entity(0, 0);
        }
    }

    public static class Builder
    {
        private int duration = 1000;
        private double speed = 5;
        private float size = 20;
        private int color = Color.BLACK;
        private int particleCount = 20;
        private double startRadians = 0;
        private double endRadians = 2 * Math.PI;

        public Builder()
        {

        }

        public BurstParticleEffect build()
        {
            return new BurstParticleEffect(size, speed, color, particleCount, startRadians, endRadians, duration);
        }

        public Builder speed(double particleSpeed)
        {
            this.speed = particleSpeed;
            return this;
        }

        public Builder duration(int duration)
        {
            this.duration = duration;
            return this;
        }

        public Builder color(int color)
        {
            this.color = color;
            return this;
        }

        //Width/Height or square
        public Builder size(float size)
        {
            this.size = size;
            return this;
        }

        public Builder particleCount(int particleCount)
        {
            this.particleCount = particleCount;
            return this;
        }

        public Builder spanDegrees(double startDegrees, double endDegrees)
        {
            return spanRadians(Math.toRadians(startDegrees), Math.toRadians(endDegrees));
        }

        public Builder spanRadians(double startRadians, double endRadians)
        {
            this.startRadians = startRadians;
            this.endRadians = endRadians;
            return this;
        }
    }*/
}
