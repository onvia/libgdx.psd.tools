package gdx.keyroy.data.tools.models;

import com.keyroy.util.json.JsonAn;

//�� Ԫ��
public class ClassElement {
	// ID
	@FieldAn(lock = true)
	protected String objId;
	// ����
	protected Object object;

	@JsonAn(skip = true)
	protected ClassPath parent;

	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	protected void setParent(ClassPath parent) {
		this.parent = parent;
	}

	public ClassPath getParent() {
		return parent;
	}

}
