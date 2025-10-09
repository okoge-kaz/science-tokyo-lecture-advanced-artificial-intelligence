import pandas as pd
import matplotlib.pyplot as plt

files = {
    "P7": "output/report01/RexJggKTabletP7K5.csv",
    "P14": "output/report01/RexJggKTabletP14K5.csv",
    "P28": "output/report01/RexJggKTabletP28K5.csv",
}

plt.figure(figsize=(12, 8))

# 色とマーカーの設定
colors = {"P7": "blue", "P14": "green", "P28": "red"}
markers = ["o", "s", "^"]  # 1st, 2nd, 3rd trial

# 各ファイルを読み込んでプロット
for label, filepath in files.items():
    df = pd.read_csv(filepath)
    x = df["NoOfEvals"]

    # 各trialをプロット
    trial_columns = [col for col in df.columns if col != "NoOfEvals"]
    for i, col in enumerate(trial_columns):
        trial_label = f"{label} - Trial {i + 1}"
        plt.plot(
            x, df[col], marker=markers[i], label=trial_label, color=colors[label], alpha=0.7, markersize=6, linewidth=2
        )

# y軸を対数スケールに設定
plt.yscale("log")

# グラフの装飾
plt.xlabel("Number of Evaluations", fontsize=12)
plt.ylabel("Value (Log Scale)", fontsize=12)
plt.title("Comparison of P7, P14, and P28 across Trials", fontsize=14)
plt.legend(bbox_to_anchor=(1.05, 1), loc="upper left")
plt.grid(True, which="both", ls="-", alpha=0.3)
plt.tight_layout()

# グラフの保存と表示
plt.savefig("figures/report01.png", dpi=300, bbox_inches="tight")
plt.show()
