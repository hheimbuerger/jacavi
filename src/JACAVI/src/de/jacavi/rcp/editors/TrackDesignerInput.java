package de.jacavi.rcp.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class TrackDesignerInput implements IEditorInput {

	private final String trackName;
	
	public TrackDesignerInput(String trackName) {
		this.trackName = trackName;
	}
	
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return trackName;
	}

	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToolTipText() {
		return "Track Editor";
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}

		if (obj instanceof TrackDesignerInput) {
			return trackName.equals(((TrackDesignerInput) obj).getName());
		}
		return false;

	}



	@Override
	public int hashCode() {
		return trackName.hashCode();
	}

}
