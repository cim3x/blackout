package com.firkinofbrain.blackout.database;

public class ModelBase {
	protected long id;
	
	public long getId(){
		return this.id;
	}
	
	public void setId(long id){
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o){
			return true;
		}
		if(o == null){
			return false;
		}
		if(!(o instanceof ModelBase)){
			return false;
		}
		ModelBase other = (ModelBase) o;
		if(this.id != other.id){
			return false;
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.id ^ (this.id >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "ModelBase [id=" + this.id + "]";
	}
	
	
}
