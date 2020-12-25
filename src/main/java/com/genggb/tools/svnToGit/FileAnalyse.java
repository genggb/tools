package com.genggb.tools.svnToGit;

import java.util.ArrayList;
import java.util.List;

public class FileAnalyse {
    public final static int NEXT_SUCCESS = 1;
    public final static int NEXT_ERROR = -1;
    public final static int NEXT_END = 0;

    private List<String> lineList;
    private int lineLength;
    private List<String> partList;
    private int partLength;
    private List<String> modifyList;
    private int modifyLength;
    private List<String> originList;
    private int originLength;
    private int index;
    private String origin;
    private String modify;

    public FileAnalyse(List<String> lineList) {
        this.lineList = lineList;
        this.lineLength = lineList.size();
        this.index = 0;
    }

    public int nextPart() {
        try {
            if (lineLength <= 0) {
                return NEXT_ERROR;
            } else if (index >= lineLength) {
                return NEXT_END;
            }
            List<Integer> list = new ArrayList<>();
            for (int i = index; i < lineLength; i++) {
                if (lineList.get(i).startsWith("@@")) {
                    list.add(i);
                }
                if (list.size() == 2) {
                    index = i;
                    break;
                }
            }
            if (list.size() > 1) {
                this.partList = lineList.subList(list.get(0) + 1, list.get(1));
                this.partLength = partList.size();
            } else {
                this.partList = lineList.subList(list.get(0) + 1, lineLength);
                this.partLength = partList.size();
                return NEXT_END;
            }
            return NEXT_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return NEXT_ERROR;
        }
    }


    public void AnalyPart() {

        int i = 0;
        List<String> modifyList = new ArrayList<>();
        List<String> orginalList = new ArrayList<>();
        StringBuilder modifySb = new StringBuilder();
        StringBuilder orginalSb = new StringBuilder();
        for (String line : partList) {
            String lineStr = line.substring(1);
            if (line.startsWith("-")) {
                modifySb.append(lineStr).append("\n");
                modifyList.add(line);
            } else if (line.startsWith("+")) {
                orginalSb.append(lineStr).append("\n");
                orginalList.add(line);
            } else {
                modifySb.append(lineStr).append("\n");
                orginalSb.append(lineStr).append("\n");
                modifyList.add(line);
                orginalList.add(line);
            }
        }
        this.modifyList = modifyList;
        this.originList = orginalList;
        this.modify = modifySb.toString();
        this.origin = orginalSb.toString();
    }

    public List<String> getLineList() {
        return lineList;
    }

    public void setLineList(List<String> lineList) {
        this.lineList = lineList;
    }

    public int getLineLength() {
        return lineLength;
    }

    public void setLineLength(int lineLength) {
        this.lineLength = lineLength;
    }

    public List<String> getPartList() {
        return partList;
    }

    public void setPartList(List<String> partList) {
        this.partList = partList;
    }

    public int getPartLength() {
        return partLength;
    }

    public void setPartLength(int partLength) {
        this.partLength = partLength;
    }

    public List<String> getModifyList() {
        return modifyList;
    }

    public void setModifyList(List<String> modifyList) {
        this.modifyList = modifyList;
    }

    public int getModifyLength() {
        return modifyLength;
    }

    public void setModifyLength(int modifyLength) {
        this.modifyLength = modifyLength;
    }

    public List<String> getOriginList() {
        return originList;
    }

    public void setOriginList(List<String> originList) {
        this.originList = originList;
    }

    public int getOriginLength() {
        return originLength;
    }

    public void setOriginLength(int originLength) {
        this.originLength = originLength;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getModify() {
        return modify;
    }

    public void setModify(String modify) {
        this.modify = modify;
    }
}
