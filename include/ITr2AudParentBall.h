// Copyright © 2026 CCP ehf.
//
//    Description:
//      An interface for telling an audio emitter which ball it is mounted on.

#pragma once

#ifndef ITr2AudParentBall_h_
#define ITr2AudParentBall_h_

BLUE_INTERFACE( ITr2AudParentBall ) :
	public IRoot
{
	/**
	 * @brief Sets the ball the emitter is mounted on, so the ball's own geometry does not occlude it.
	 *
	 * The ball is passed through to the sightline query untouched, audio never looks inside it.
	 * The emitter holds it weakly. A separate interface from ITr2AudEmitter so an older audio
	 * build fails the cast instead of being called through the wrong vtable slot.
	 *
	 * @param ball The destiny ball the owner is attached to (its translationCurve), or nullptr when free.
	 */
	virtual void SetParentBall( IRoot * ball ) = 0;
};

#endif
