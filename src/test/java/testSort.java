public class testSort {
    public static void main(String[] args) {
        double[] list={5,3,1,2};
        int[] res;
        Sort sort= new Sort(list);
        res=sort.run();
        for(int i:res){
            System.out.print(i+" ");
        }
    }
}
