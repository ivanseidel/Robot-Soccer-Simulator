import processing.core.*;

public class Simulatable{

	// Current Force, Acceleration, Speed and Position
	protected PVector force = new PVector();
	protected PVector accel = new PVector();
	protected PVector speed = new PVector();
	protected PVector position = new PVector();

	// Real Acceleration and Speed
	protected PVector realAccel = new PVector();
	protected PVector realSpeed = new PVector();

	public float getMass(){
		return 1f;
	}

	public float getKFactor(){
		return 1f;
	}

	/*
		This method should return a force for the given dt
	*/
	public PVector getForce(float dt){
		return force.get();
	}

	/*
		Returns the REAL Acceleration, Speed and Position
	*/

	public PVector getRealAccel(){
		return realAccel.get();
	}

	public PVector getRealSpeed(){
		return realSpeed.get();
	}

	public PVector getRealPosition(){
		return position.get();
	}

	public void simulate(float dt){
		// Saves last Speed and Position to calculate the real Accel and Speed
		PVector lastSpeed = speed.get();
		PVector lastPosition = position.get();
		
		// Simulate Acceleration
		accel = getForce(dt);
		accel.div(getMass());
		force.set(0,0);

		// Simulate Speed
		PVector dSpeed = accel.get();
		dSpeed.mult(dt);
		speed.add(dSpeed);

		// Simulate Position
		PVector dPos = speed.get();
		dPos.mult(dt);
		position.add(dPos);

		// Calculate real Accel and Speed
		realAccel = speed.get();
		realAccel.sub(lastSpeed);
		realAccel.div(dt);

		realSpeed = position.get();
		realSpeed.sub(lastPosition);
		realSpeed.div(dt);
	}

	public boolean canCollide(Simulatable s){
		return true;
	}

	public boolean colliding(Simulatable that){
		if(this instanceof ShapeCircle && that instanceof ShapeCircle){
			float dist = PVector.sub(this.position, that.position).mag();
			if(dist <= ((ShapeCircle)this).getRadius() + ((ShapeCircle)that).getRadius()){
				// System.out.println("Collide CIRCLE w CIRCLE: "+this+" with "+that);
				return true;
			}
		}else if(this instanceof ShapeCircle && that instanceof ShapeRect ||
				 that instanceof ShapeCircle && this instanceof ShapeRect){
			PVector dist = PVector.sub(this.position, that.position);

			ShapeCircle c = (ShapeCircle)(this instanceof ShapeCircle ? this : that);
			ShapeRect r = (ShapeRect)(this instanceof ShapeRect ? this : that);

			PVector cPos = ((Simulatable)c).position;
			PVector rPos = ((Simulatable)r).position;
			float radius = c.getRadius();
			
			if( cPos.x >= rPos.x - r.getWidth()/2 - radius &&
				cPos.x <= rPos.x + r.getWidth()/2 + radius &&
				cPos.y >= rPos.y - r.getHeight()/2 - radius &&
				cPos.y <= rPos.y + r.getHeight()/2 + radius){
				
				// System.out.println("Collide BLOCK w CIRCLE: "+this+" with "+that);

				// Rough check is OK, let's do the actual verification now
				// Two cases for collision: some segment intersects circle,
				// or circle inside rect
				return MathUtil.distCircleToRect(c, cPos, r, rPos) <= 0f ||
					   MathUtil.pointInsideRect(cPos, r, rPos);
			}
		}else{
			System.out.println("Exception: Cannot handle Collision with unknown types");
		}
		return false;
	}

	public void resolveCollision(Simulatable that){

		float thisMass = this.getMass();
		float thatMass = that.getMass();

		PVector thisSpeed = this.speed;
		PVector thatSpeed = that.speed;

		PVector P = PVector.sub(that.position, this.position);
		float dist = 0f;

		// Set back and find out normal reaction point
		if(this instanceof ShapeCircle && that instanceof ShapeCircle){

			float thisRadius = ((ShapeCircle)this).getRadius();
			float thatRadius = ((ShapeCircle)that).getRadius();

			// set back both balls along collision direction so that they are just touching again
			// Use their speed as "weighening" of this shift
			dist = P.mag() - thisRadius - thatRadius;

			// parallel vector in collision direction
			P.normalize();

		}else if(this instanceof ShapeCircle && that instanceof ShapeRect ||
				 that instanceof ShapeCircle && this instanceof ShapeRect){

			ShapeCircle c = (ShapeCircle)(this instanceof ShapeCircle ? this : that);
			ShapeRect r = (ShapeRect)(this instanceof ShapeRect ? this : that);

			PVector cPos = ((Simulatable) c).position;
			PVector rPos = ((Simulatable) r).position;

			dist = MathUtil.distCircleToRect(c, cPos, r, rPos);

			PVector collisionPoint = MathUtil.closestToCircleInRect(c, cPos, r, rPos);
			P = PVector.sub(collisionPoint, cPos);
			P.normalize();

		}else{
			System.out.println("Impossible to collide: "+this+" with "+that);
		}

		// Correct overlapping by moving Simulatables
		float thisMag = (this.canCollide(that) ? thisSpeed.mag(): 0);
		float thatMag = (that.canCollide(this) ? thatSpeed.mag(): 0);

		if(thisMag + thatMag <= 0.0){
			thisMag = 1f;
		}

		this.position.add(PVector.mult(P, dist * thisMag / (thisMag + thatMag)));

		if(that.canCollide(this))
			that.position.add(PVector.mult(P, dist * thatMag / (thisMag + thatMag) * -1));

		if(Double.isNaN(force.x) || 
			Double.isNaN(speed.x) || 
			Double.isNaN(accel.x) || 
			Double.isNaN(position.x) ||
			Double.isNaN(P.x)){

			System.out.println("$$$COL "+this+"with"+that+" - "+force+""+accel+""+speed+""+position+""+P+" "+thisMag+"/"+thatMag);
		}

		// calculate speed components along and normal to collision direction
		PVector N = new PVector(P.y, P.x * -1); // normal vector to collision direction
		float vp1 = thisSpeed.dot(P);     // velocity of P1 along collision direction
		float vn1 = thisSpeed.dot(N);     // velocity of P1 normal to collision direction
		float vp2 = thatSpeed.dot(P);     // velocity of P2 along collision direction
		float vn2 = thatSpeed.dot(N);     // velocity of P2 normal to collision direction
		
		// calculate collison
		// simple collision: "flip"
		float massSum = (thisMass + thatMass);
		float k = this.getKFactor() * that.getKFactor();
		float vp1_after = 0;
		float vp2_after = 0;

		vp1_after = (-1)*((k) * thatMass * Math.abs(vp1-vp2) - thisMass * vp1 - thatMass*vp2) / massSum;
		vp2_after =      ((k) * thisMass * Math.abs(vp1-vp2) + thisMass * vp1 + thatMass*vp2) / massSum;

		this.speed = PVector.add(PVector.mult(P, vp1_after),PVector.mult(N, vn1));

		if(that.canCollide(this))
			that.speed = PVector.add(PVector.mult(P, vp2_after),PVector.mult(N, vn2));
	}
	
}