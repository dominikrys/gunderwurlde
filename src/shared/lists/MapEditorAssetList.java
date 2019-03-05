package shared.lists;

public enum MapEditorAssetList {
	
	ARROW("file:assets/img/map_editor/arrow.png"),
	DOT("file:assets/img/map_editor/dot.png");
	

private String assetPath;
	
	MapEditorAssetList(String assetPath) {
        this.assetPath = assetPath;
    }

    public String getPath() {
        return assetPath;
    }
	
}
