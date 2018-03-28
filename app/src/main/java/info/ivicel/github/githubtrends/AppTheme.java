package info.ivicel.github.githubtrends;


public enum AppTheme {
    Cyan(R.style.CyanTheme),
    Indigo(R.style.IndigoTheme),
    Green(R.style.GreenTheme),
    Red(R.style.RedTheme),
    BlueGrey(R.style.BlueGreyTheme),
    Black(R.style.BlackTheme),
    Purple(R.style.PurpleTheme),
    Orange(R.style.OrangeTheme),
    Pink(R.style.PinkTheme);
    
    private int res;
    
    AppTheme(int res) {
        this.res = res;
    }
    
    public int v() {
        return res;
    }
}
