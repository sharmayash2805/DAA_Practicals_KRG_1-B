class Solution {
  public:
    vector<vector<int>> countFreq(vector<int>& arr) {
        // code here
        unordered_map<int, int> freq;
        for (int x : arr) {
            freq[x]++;
        }

        vector<vector<int>> res;
        for (const auto &p : freq) {
            res.push_back({p.first, p.second});
        }

        sort(res.begin(), res.end(), [](const vector<int> &a, const vector<int> &b) {
            return a[0] < b[0];
        });

        return res;
    }
};