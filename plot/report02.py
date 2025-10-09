import pandas as pd
import matplotlib.pyplot as plt

# Kのパラメータリスト
k_values = ["K2", "K5", "K10", "K15"]

# 色とマーカーの設定
colors = {"K2": "blue", "K5": "green", "K10": "red", "K15": "purple"}
markers = ["o", "s", "^"]  # 1st, 2nd, 3rd trial

plt.figure(figsize=(12, 8))

# 各Kについてプロット
for k in k_values:
    filepath = f"output/report02/RexJggKTabletP14{k}.csv"

    try:
        df = pd.read_csv(filepath)
        x = df["NoOfEvals"]

        # 各trialをプロット
        trial_columns = [col for col in df.columns if col != "NoOfEvals"]
        for i, col in enumerate(trial_columns):
            trial_label = f"{k} - Trial {i + 1}"
            plt.plot(
                x, df[col], marker=markers[i], label=trial_label, color=colors[k], alpha=0.7, markersize=6, linewidth=2
            )
    except FileNotFoundError:
        print(f"Warning: {filepath} not found")
        continue

# y軸を対数スケールに設定
plt.yscale("log")

# グラフの装飾
plt.xlabel("Number of Evaluations", fontsize=12)
plt.ylabel("Value (Log Scale)", fontsize=12)
plt.title("Comparison of K2, K5, K10, and K15 for P14 across Trials", fontsize=14)
plt.legend(bbox_to_anchor=(1.05, 1), loc="upper left")
plt.grid(True, which="both", ls="-", alpha=0.3)
plt.tight_layout()

# グラフの保存と表示
plt.savefig("figures/report02.png", dpi=300, bbox_inches="tight")
plt.show()

print("Plot has been generated successfully!")
