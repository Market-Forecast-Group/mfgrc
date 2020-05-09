package com.mfg.strategy.manual;


public enum Trailing {
	RC {
		@Override
		public boolean isTrailing(TrailingStatus status, Routing routing) {
			if (routing == Routing.LONG) {
				return status.isLongRC();
			}
			return status.isShortRC();
		}

		@Override
		public void setTrailing(TrailingStatus status, Routing routing,
				boolean enabled) {
			if (routing == Routing.LONG) {
				status.setLongRC(enabled);
			} else {
				status.setShortRC(enabled);
			}
		}

	},
	CL {

		@Override
		public boolean isTrailing(TrailingStatus status, Routing routing) {
			if (routing == Routing.LONG) {
				return status.isLongCL();
			}
			return status.isShortCL();
		}

		@Override
		public void setTrailing(TrailingStatus status, Routing routing,
				boolean enabled) {
			if (routing == Routing.LONG) {
				status.setLongCL(enabled);
			} else {
				status.setShortCL(enabled);
			}

		}
	},
	SC {

		@Override
		public boolean isTrailing(TrailingStatus status, Routing routing) {
			if (routing == Routing.LONG) {
				return status.isLongSC();
			}
			return status.isShortSC();
		}

		@Override
		public void setTrailing(TrailingStatus status, Routing routing,
				boolean enabled) {
			if (routing == Routing.LONG) {
				status.setLongSC(enabled);
			} else {
				status.setShortSC(enabled);
			}

		}
	};

	public void revertTrail(TrailingStatus status, Routing routing) {
		boolean enabled = !isTrailing(status, routing);
		setTrailing(status, routing, enabled);
	}

	public abstract boolean isTrailing(TrailingStatus status, Routing routing);

	public abstract void setTrailing(TrailingStatus status, Routing routing,
			boolean enabled);
}
