package my.quartz.alone.entity;

// 规则实体
public class RegulationEntity {
    int type;    // -1 表示小于； 0 表示等于； 1表示大于；
    float expectedSize;    // 预期文件大小

    String name;

    public RegulationEntity(String name, int type, float expectedSize) {
        this.name = name;
        this.type = type;
        this.expectedSize = expectedSize;
    }

    public boolean hitRegulation(float fileSize) {
        switch (type) {
            case -1:
                return fileSize < expectedSize;
            case 0:
                return Math.abs(fileSize - expectedSize) < Float.MIN_VALUE;
            case 1:
                return fileSize > expectedSize;
        }
        return false;  // type不合规时，返回false
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
