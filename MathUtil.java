import processing.core.*;

class MathUtil {
	/**
	 * Put angle in degrees into [0, 360) range
	 */
	public static float fixAngle(float angle) {
		while (angle < 0f)
			angle += 360f;
		while (angle >= 360f)
			angle -= 360f;
		return angle;
	}

	public static float relativeAngle(float delta){
		while (delta < 180f)
			delta += 360f;
		while (delta >= 180f)
			delta -= 360f;
		return delta;
	}

	public static float rayDistance(PVector origin, float direction, Simulatable sim) {
		// FIXME this is ugly and I should be ashamed!
		if (sim instanceof ShapeCircle) {
			/* Inefficient, but simple approach:
			 * Start with the distance to the circle, and try to proceed
			 * by that distance. If we are going to cross the border, back off and start
			 * over with half the previous distance.
			 * If the ray can see the circle, we end up very close to it.
			 */
			ShapeCircle circle = (ShapeCircle) sim;
			PVector centre = sim.getRealPosition();
			float radius = circle.getRadius();

			PVector currentPos = origin.get();

			// use a slightly shorter distance to prevent overshooting
			float mult = .9f;

			PVector stepVector = PVector.fromAngle(direction);
			stepVector.setMag(mult * distPointToCircle(currentPos, centre, radius));

			while (stepVector.mag() > 1e-5f) {
				PVector newPos = PVector.add(currentPos, stepVector);
				float newDist = distPointToCircle(newPos, centre, radius);

				if (newDist < 0f) {
					stepVector.setMag(stepVector.mag() / 2f);
				}
				else if (newDist > distPointToCircle(currentPos, centre, radius))
					break;
				else {
					currentPos = newPos;
					stepVector.setMag(mult * newDist);
				}
			}

			if (distPointToCircle(currentPos, centre, radius) < 1e-4f)
				return origin.dist(currentPos);
			return Float.POSITIVE_INFINITY;
		}
		else if (sim instanceof ShapeRect) {
			float dist = Float.POSITIVE_INFINITY;

			// endpoints
			ShapeRect rect = (ShapeRect) sim;
			PVector centre = sim.getRealPosition();
			float end_x0 = centre.x - rect.getWidth() / 2.0f;
			float end_x1 = centre.x + rect.getWidth() / 2.0f;
			float end_y0 = centre.y - rect.getHeight() / 2.0f;
			float end_y1 = centre.y + rect.getHeight() / 2.0f;
			float xs[] = new float[] { end_x0, end_x1, end_x1, end_x0 };
			float ys[] = new float[] { end_y0, end_y0, end_y1, end_y1 };

			// edges
			for (int i = 0; i < 4; i++) {
				float x0 = xs[i];
				float y0 = ys[i];
				float x1 = xs[(i + 1) % 4];
				float y1 = ys[(i + 1) % 4];

				dist = Math.min(dist, distRayToSegment(origin, direction, x0, y0, x1, y1));
			}

			return dist;
		}
		else
			return Float.POSITIVE_INFINITY;
	}

	public static boolean pointInsideRect(PVector point, ShapeRect r, PVector rPos) {
		float x0 = rPos.x - r.getWidth() / 2.0f;
		float x1 = rPos.x + r.getWidth() / 2.0f;
		float y0 = rPos.y - r.getHeight() / 2.0f;
		float y1 = rPos.y + r.getHeight() / 2.0f;

		return x0 <= point.x && point.x <= x1 && y0 <= point.y && point.y <= y1;
	}

	public static float distCircleToRect(ShapeCircle c, PVector cPos, ShapeRect r, PVector rPos) {
		return distPointToCircle(
			closestToCircleInRect(c, cPos, r, rPos),
			cPos,
			c.getRadius()
		);
	}

	public static PVector closestToCircleInRect(ShapeCircle c, PVector cPos, ShapeRect r, PVector rPos) {
		float minDist = Float.POSITIVE_INFINITY;

		// endpoints
		float end_x0 = rPos.x - r.getWidth() / 2.0f;
		float end_x1 = rPos.x + r.getWidth() / 2.0f;
		float end_y0 = rPos.y - r.getHeight() / 2.0f;
		float end_y1 = rPos.y + r.getHeight() / 2.0f;
		float xs[] = new float[] { end_x0, end_x1, end_x1, end_x0 };
		float ys[] = new float[] { end_y0, end_y0, end_y1, end_y1 };

		PVector point = null;

		// edges
		for (int i = 0; i < 4; i++) {
			float x0 = xs[i];
			float y0 = ys[i];
			float x1 = xs[(i + 1) % 4];
			float y1 = ys[(i + 1) % 4];

			float dist = distCircleToSegment(c, cPos, x0, y0, x1, y1);
			if (dist < minDist) {
				minDist = dist;
				point = closestToCircleInSegment(c, cPos, x0, y0, x1, y1);
			}
		}

		if (point == null) {
			System.out.println("WARNING: could not find closest point to circle " + c + " at " + cPos);
			point = rPos.get();
			point.x -= r.getWidth() / 2.0f;
			point.y -= r.getHeight() / 2.0f;
		}

		return point;
	}

	private static float distCircleToSegment(ShapeCircle c, PVector cPos, float x0, float y0, float x1, float y1) {
		return distPointToCircle(
			closestToCircleInSegment(c, cPos, x0, y0, x1, y1),
			cPos,
			c.getRadius()
		);
	}

	private static PVector closestToCircleInSegment(ShapeCircle c, PVector cPos, float x0, float y0, float x1, float y1) {
		PVector segUnit = new PVector(x1 - x0, y1 - y0);
		segUnit.normalize();

		PVector v = new PVector(cPos.x - x0, cPos.y - y0);

		float magAlongSeg = v.dot(segUnit);

		PVector closest;
		if (magAlongSeg <= 0f)
			closest = new PVector(x0, y0);
		else if (magAlongSeg >= (new PVector(x1 - x0, y1 - y0)).mag())
			closest = new PVector(x1, y1);
		else {
			closest = new PVector(x0, y0);
			PVector u = segUnit.get();
			u.mult(magAlongSeg);
			closest.add(u);
		}

		return closest;
	}

	private static float distPointToSegment(PVector point, float x0, float y0, float x1, float y1) {
		float dot = (point.x - x0) * (x1 - x0) + (point.y - y0) * (y1 - y0);
		float lenSq = (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0);

		float frac = dot / lenSq;

		float x = x0, y = y0;

		if (frac > 1f) {
			x = x1;
			y = y1;
		}
		else if (frac >= 0f) {
			x = x0 + frac * (x1 - x0);
			y = y0 + frac * (y1 - y0);
		}

		return (float) Math.sqrt((x - point.x) * (x - point.x) + (y - point.y) * (y - point.y));
	}

	private static float distRayToSegment(PVector origin, float direction, float x0, float y0, float x1, float y1) {
		// ternary search
		float minDist = 0f, maxDist = 10f;
		while (maxDist - minDist > 1e-5f) {
			float mid1 = minDist + (maxDist - minDist) / 3.0f;
			float mid2 = minDist + (maxDist - minDist) / 1.5f;

			PVector u = PVector.fromAngle(direction);

			u.setMag(mid1);
			PVector point1 = PVector.add(origin, u);

			u.setMag(mid2);
			PVector point2 = PVector.add(origin, u);

			float dist1 = distPointToSegment(point1, x0, y0, x1, y1);
			float dist2 = distPointToSegment(point2, x0, y0, x1, y1);

			if (dist1 < dist2)
				maxDist = mid2;
			else
				minDist = mid1;

			if (Math.abs(dist1) < 1e-4f)
				return mid1;
			if (Math.abs(dist2) < 1e-4f)
				return mid2;
		}

		return Float.POSITIVE_INFINITY;
	}

	/*
	This has to be debugged! more efficient, though
	private static float distRayToSegment(PVector origin, float direction, float x0, float y0, float x1, float y1) {
		// black magic a.k.a. vector algebra
		// <http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect>
		PVector p = origin.get();
		PVector q = new PVector(x0, y0);
		PVector r = PVector.fromAngle(direction);
		PVector s = new PVector(x1 - x0, y1 - y0);

		PVector u1 = p.get();
		u1.sub(q);
		PVector u2 = u1.cross(r);
		PVector u3 = r.cross(s);

		if (Math.abs(u3.mag()) < 1e-6f)
			return Float.POSITIVE_INFINITY;

		float coef = u2.mag() / u3.mag();
		if (coef <= 0f || coef >= 1f)
			return Float.POSITIVE_INFINITY;

		PVector t1 = q.get();
		t1.sub(p);
		PVector t2 = t1.cross(s);
		PVector t3 = u3.get();

		float coef2 = t2.mag() / t3.mag();
		// do not detect objects behind the ray
		if (coef2 < 0f)
			return Float.POSITIVE_INFINITY;

		PVector point = q.get();
		PVector s2 = s;
		s2.mult(coef);
		point.add(s2);

		return origin.dist(point);
	}*/

	private static float distPointToCircle(PVector point, PVector centre, float radius) {
		return point.dist(centre) - radius;
	}
}